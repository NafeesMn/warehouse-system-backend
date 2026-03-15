package com.artiselite.warehouse.product.service;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.product.dto.request.CreateProductRequest;
import com.artiselite.warehouse.product.dto.request.UpdateProductRequest;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    PagedResponse<ProductListItemResponse> getProducts(int page, int size, String sortBy, String sortDirection);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    PagedResponse<ProductListItemResponse> searchProducts(
            String keyword,
            String tag,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}