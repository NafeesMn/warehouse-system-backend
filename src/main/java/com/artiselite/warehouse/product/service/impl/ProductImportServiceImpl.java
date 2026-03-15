package com.artiselite.warehouse.product.service.impl;

import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.product.dto.response.ProductImportResponse;
import com.artiselite.warehouse.product.dto.response.ProductImportRowErrorResponse;
import com.artiselite.warehouse.product.entity.Product;
import com.artiselite.warehouse.product.repository.ProductRepository;
import com.artiselite.warehouse.product.service.ProductImportService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductImportServiceImpl implements ProductImportService {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "sku",
            "name",
            "description",
            "tags",
            "unitprice",
            "currentstock",
            "reorderlevel"
    );

    private final ProductRepository productRepository;

    public ProductImportServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductImportResponse importProducts(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("A CSV file is required.");
        }

        List<ProductImportRowErrorResponse> errors = new ArrayList<>();
        int insertedCount = 0;
        int updatedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new BadRequestException("The uploaded CSV file is empty.");
            }

            Map<String, Integer> headerIndex = buildHeaderIndex(parseCsvLine(removeBom(headerLine)));
            validateHeaders(headerIndex);

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) {
                    continue;
                }

                List<String> values;
                try {
                    values = parseCsvLine(line);
                } catch (BadRequestException exception) {
                    errors.add(new ProductImportRowErrorResponse(rowNumber, null, exception.getMessage()));
                    continue;
                }

                String sku = readValue(values, headerIndex, "sku");
                try {
                    ProductCsvRow row = toRow(values, headerIndex);
                    Optional<Product> existingProduct = productRepository.findBySkuIgnoreCase(row.sku());
                    Product product = existingProduct.orElseGet(Product::new);
                    applyRow(product, row);
                    productRepository.save(product);
                    if (existingProduct.isPresent()) {
                        updatedCount++;
                    } else {
                        insertedCount++;
                    }
                } catch (RuntimeException exception) {
                    errors.add(new ProductImportRowErrorResponse(rowNumber, normalizeOptionalText(sku), exception.getMessage()));
                }
            }
        } catch (IOException exception) {
            throw new BadRequestException("Failed to read the uploaded CSV file.");
        }

        return new ProductImportResponse(insertedCount, updatedCount, errors.size(), errors);
    }

    private void applyRow(Product product, ProductCsvRow row) {
        product.setSku(row.sku());
        product.setName(row.name());
        product.setDescription(row.description());
        product.setTags(row.tags());
        product.setUnitPrice(row.unitPrice());
        product.setCurrentStock(row.currentStock());
        product.setReorderLevel(row.reorderLevel());
    }

    private ProductCsvRow toRow(List<String> values, Map<String, Integer> headerIndex) {
        String sku = normalizeRequiredText(readValue(values, headerIndex, "sku"), "SKU is required.");
        String name = normalizeRequiredText(readValue(values, headerIndex, "name"), "Product name is required.");
        String description = normalizeOptionalText(readValue(values, headerIndex, "description"));
        String tags = normalizeOptionalText(readValue(values, headerIndex, "tags"));
        BigDecimal unitPrice = parseUnitPrice(readValue(values, headerIndex, "unitprice"));
        Integer currentStock = parseNonNegativeInteger(readValue(values, headerIndex, "currentstock"), "currentStock");
        Integer reorderLevel = parseNonNegativeInteger(readValue(values, headerIndex, "reorderlevel"), "reorderLevel");
        return new ProductCsvRow(sku, name, description, tags, unitPrice, currentStock, reorderLevel);
    }

    private Map<String, Integer> buildHeaderIndex(List<String> headers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String normalizedHeader = normalizeHeader(headers.get(i));
            if (StringUtils.hasText(normalizedHeader)) {
                index.putIfAbsent(normalizedHeader, i);
            }
        }
        return index;
    }

    private void validateHeaders(Map<String, Integer> headerIndex) {
        for (String requiredHeader : REQUIRED_HEADERS) {
            if (!headerIndex.containsKey(requiredHeader)) {
                throw new BadRequestException("Missing required CSV column: " + requiredHeader);
            }
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);
            if (currentChar == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (currentChar == ',' && !inQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentChar);
            }
        }

        if (inQuotes) {
            throw new BadRequestException("Malformed CSV row: unmatched quote detected.");
        }

        values.add(currentValue.toString());
        return values;
    }

    private String readValue(List<String> values, Map<String, Integer> headerIndex, String headerName) {
        Integer index = headerIndex.get(headerName);
        if (index == null || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    private Integer parseNonNegativeInteger(String rawValue, String fieldName) {
        String normalized = normalizeRequiredText(rawValue, fieldName + " is required.");
        try {
            int parsedValue = Integer.parseInt(normalized);
            if (parsedValue < 0) {
                throw new BadRequestException(fieldName + " must not be negative.");
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new BadRequestException(fieldName + " must be a valid integer.");
        }
    }

    private BigDecimal parseUnitPrice(String rawValue) {
        String normalized = normalizeOptionalText(rawValue);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        try {
            BigDecimal parsedValue = new BigDecimal(normalized);
            if (parsedValue.signum() < 0) {
                throw new BadRequestException("unitPrice must not be negative.");
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new BadRequestException("unitPrice must be a valid decimal number.");
        }
    }

    private String normalizeRequiredText(String value, String message) {
        String normalized = normalizeOptionalText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeHeader(String value) {
        String normalized = normalizeOptionalText(value);
        return normalized == null ? null : normalized.replace("_", "").toLowerCase(Locale.ROOT);
    }

    private String removeBom(String value) {
        return value != null && value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    private record ProductCsvRow(
            String sku,
            String name,
            String description,
            String tags,
            BigDecimal unitPrice,
            Integer currentStock,
            Integer reorderLevel
    ) {
    }
}