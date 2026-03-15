package com.artiselite.warehouse.outbound.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionRequest;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import com.artiselite.warehouse.outbound.service.OutboundTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/outbounds")
public class OutboundTransactionController {

    private final OutboundTransactionService outboundTransactionService;

    public OutboundTransactionController(OutboundTransactionService outboundTransactionService) {
        this.outboundTransactionService = outboundTransactionService;
    }

    @GetMapping
    public ApiResponse<List<OutboundTransactionResponse>> getOutbounds() {
        return ApiResponse.success(
                "Outbound transactions loaded successfully.",
                outboundTransactionService.getAllOutboundTransactions()
        );
    }

    @GetMapping("/{outboundId}")
    public ApiResponse<OutboundTransactionResponse> getOutbound(@PathVariable Long outboundId) {
        return ApiResponse.success(
                "Outbound transaction loaded successfully.",
                outboundTransactionService.getOutboundTransactionById(outboundId)
        );
    }

    @PostMapping
    public ApiResponse<OutboundTransactionResponse> createOutbound(
            @Valid @RequestBody OutboundTransactionRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Outbound transaction created successfully.",
                outboundTransactionService.createOutboundTransaction(request, authentication.getName())
        );
    }
}
