package com.artiselite.warehouse.product.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.product.dto.ProductRequest;
import com.artiselite.warehouse.product.dto.ProductResponse;
import com.artiselite.warehouse.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        return ApiResponse.success("Products loaded successfully.", productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.success("Product loaded successfully.", productService.getProductById(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success("Product created successfully.", productService.createProduct(request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request
    ) {
        return ApiResponse.success(
                "Product updated successfully.",
                productService.updateProduct(productId, request)
        );
    }
}
