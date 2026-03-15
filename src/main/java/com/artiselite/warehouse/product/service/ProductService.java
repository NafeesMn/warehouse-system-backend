package com.artiselite.warehouse.product.service;

import com.artiselite.warehouse.product.dto.ProductRequest;
import com.artiselite.warehouse.product.dto.ProductResponse;
import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long productId);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long productId, ProductRequest request);
}
