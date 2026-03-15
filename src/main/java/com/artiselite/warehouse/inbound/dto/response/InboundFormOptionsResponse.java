package com.artiselite.warehouse.inbound.dto.response;

import com.artiselite.warehouse.common.dto.ProductReferenceOptionResponse;
import com.artiselite.warehouse.common.dto.ReferenceOptionResponse;
import java.util.List;

public record InboundFormOptionsResponse(
        List<ProductReferenceOptionResponse> products,
        List<ReferenceOptionResponse> suppliers
) {
}