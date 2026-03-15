package com.artiselite.warehouse.inbound.service;

import com.artiselite.warehouse.inbound.dto.InboundTransactionRequest;
import com.artiselite.warehouse.inbound.dto.InboundTransactionResponse;
import java.util.List;

public interface InboundTransactionService {

    List<InboundTransactionResponse> getAllInboundTransactions();

    InboundTransactionResponse getInboundTransactionById(Long inboundId);

    InboundTransactionResponse createInboundTransaction(InboundTransactionRequest request, String createdByEmail);
}
