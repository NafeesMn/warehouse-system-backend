package com.artiselite.warehouse.inbound.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.inbound.dto.InboundTransactionRequest;
import com.artiselite.warehouse.inbound.dto.InboundTransactionResponse;
import com.artiselite.warehouse.inbound.service.InboundTransactionService;
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
@RequestMapping("/api/inbounds")
public class InboundTransactionController {

    private final InboundTransactionService inboundTransactionService;

    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
        this.inboundTransactionService = inboundTransactionService;
    }

    @GetMapping
    public ApiResponse<List<InboundTransactionResponse>> getInbounds() {
        return ApiResponse.success(
                "Inbound transactions loaded successfully.",
                inboundTransactionService.getAllInboundTransactions()
        );
    }

    @GetMapping("/{inboundId}")
    public ApiResponse<InboundTransactionResponse> getInbound(@PathVariable Long inboundId) {
        return ApiResponse.success(
                "Inbound transaction loaded successfully.",
                inboundTransactionService.getInboundTransactionById(inboundId)
        );
    }

    @PostMapping
    public ApiResponse<InboundTransactionResponse> createInbound(
            @Valid @RequestBody InboundTransactionRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Inbound transaction created successfully.",
                inboundTransactionService.createInboundTransaction(request, authentication.getName())
        );
    }
}
