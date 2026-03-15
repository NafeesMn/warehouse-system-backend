package com.artiselite.warehouse.outbound.service;

import com.artiselite.warehouse.outbound.dto.OutboundTransactionRequest;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import java.util.List;

public interface OutboundTransactionService {

    List<OutboundTransactionResponse> getAllOutboundTransactions();

    OutboundTransactionResponse getOutboundTransactionById(Long outboundId);

    OutboundTransactionResponse createOutboundTransaction(OutboundTransactionRequest request, String createdByEmail);
}
