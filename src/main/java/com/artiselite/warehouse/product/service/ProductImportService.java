package com.artiselite.warehouse.product.service;

import com.artiselite.warehouse.product.dto.response.ProductImportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImportService {

    ProductImportResponse importProducts(MultipartFile file);
}