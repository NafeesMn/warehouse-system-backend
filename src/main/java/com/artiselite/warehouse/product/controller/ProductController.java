package com.artiselite.warehouse.product.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.product.dto.request.CreateProductRequest;
import com.artiselite.warehouse.product.dto.request.UpdateProductRequest;
import com.artiselite.warehouse.product.dto.response.ProductImportResponse;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.dto.response.ProductResponse;
import com.artiselite.warehouse.product.service.ProductImportService;
import com.artiselite.warehouse.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@PreAuthorize("hasRole('MANAGER')")
public class ProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;

    public ProductController(ProductService productService, ProductImportService productImportService) {
        this.productService = productService;
        this.productImportService = productImportService;
    }

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success("Product created successfully.", productService.createProduct(request));
    }

    @PostMapping("/import")
    public ApiResponse<ProductImportResponse> importProducts(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(
                "Products imported successfully.",
                productImportService.importProducts(file)
        );
    }

    @GetMapping
    public ApiResponse<PagedResponse<ProductListItemResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ApiResponse.success(
                "Products loaded successfully.",
                productService.getProducts(page, size, sortBy, sortDirection)
        );
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<ProductListItemResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ApiResponse.success(
                "Product search completed successfully.",
                productService.searchProducts(keyword, tag, page, size, sortBy, sortDirection)
        );
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.success("Product loaded successfully.", productService.getProductById(productId));
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ApiResponse.success(
                "Product updated successfully.",
                productService.updateProduct(productId, request)
        );
    }
}