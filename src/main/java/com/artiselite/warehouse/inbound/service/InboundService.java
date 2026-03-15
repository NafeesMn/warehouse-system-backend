package com.artiselite.warehouse.inbound.service;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.inbound.dto.request.CreateInboundRequest;
import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundResponse;

public interface InboundService {

    InboundResponse createInbound(CreateInboundRequest request, String createdByEmail);

    PagedResponse<InboundListItemResponse> getInbounds(
            Long productId,
            Long supplierId,
            int page,
            int size,
            String sortDirection
    );

    InboundResponse getInboundById(Long inboundId);
}