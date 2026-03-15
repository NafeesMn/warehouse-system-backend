package com.artiselite.warehouse.outbound.dto.response;

import com.artiselite.warehouse.common.dto.ProductReferenceOptionResponse;
import com.artiselite.warehouse.common.dto.ReferenceOptionResponse;
import java.util.List;

public record OutboundFormOptionsResponse(
        List<ProductReferenceOptionResponse> products,
        List<ReferenceOptionResponse> customers
) {
}