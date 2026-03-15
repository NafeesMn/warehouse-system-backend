package com.artiselite.warehouse.inbound.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.inbound.dto.request.CreateInboundRequest;
import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundResponse;
import com.artiselite.warehouse.inbound.service.InboundService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inbounds")
@PreAuthorize("hasAnyRole('MANAGER', 'OPERATOR')")
public class InboundController {

    private final InboundService inboundService;

    public InboundController(InboundService inboundService) {
        this.inboundService = inboundService;
    }

    @PostMapping
    public ApiResponse<InboundResponse> createInbound(
            @Valid @RequestBody CreateInboundRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Inbound transaction created successfully.",
                inboundService.createInbound(request, authentication.getName())
        );
    }

    @GetMapping
    public ApiResponse<PagedResponse<InboundListItemResponse>> getInbounds(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ApiResponse.success(
                "Inbound transactions loaded successfully.",
                inboundService.getInbounds(productId, supplierId, page, size, sortDirection)
        );
    }

    @GetMapping("/{inboundId}")
    public ApiResponse<InboundResponse> getInbound(@PathVariable Long inboundId) {
        return ApiResponse.success(
                "Inbound transaction loaded successfully.",
                inboundService.getInboundById(inboundId)
        );
    }
}