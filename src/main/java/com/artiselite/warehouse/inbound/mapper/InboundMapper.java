package com.artiselite.warehouse.inbound.mapper;

import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.dto.response.InboundResponse;
import com.artiselite.warehouse.inbound.entity.InboundTransaction;
import org.springframework.stereotype.Component;

@Component
public class InboundMapper {

    public InboundResponse toResponse(InboundTransaction inboundTransaction) {
        return new InboundResponse(
                inboundTransaction.getInboundId(),
                inboundTransaction.getProduct().getProductId(),
                inboundTransaction.getProduct().getSku(),
                inboundTransaction.getProduct().getName(),
                inboundTransaction.getSupplier().getSupplierId(),
                inboundTransaction.getSupplier().getSupplierName(),
                inboundTransaction.getQuantity(),
                inboundTransaction.getReceivedDate(),
                inboundTransaction.getReferenceNo(),
                inboundTransaction.getRemarks(),
                inboundTransaction.getCreatedBy().getUserId(),
                inboundTransaction.getCreatedBy().getFullName(),
                inboundTransaction.getCreatedAt(),
                inboundTransaction.getStockAfterUpdate()
        );
    }

    public InboundListItemResponse toListItemResponse(InboundTransaction inboundTransaction) {
        return new InboundListItemResponse(
                inboundTransaction.getInboundId(),
                inboundTransaction.getProduct().getProductId(),
                inboundTransaction.getProduct().getSku(),
                inboundTransaction.getProduct().getName(),
                inboundTransaction.getSupplier().getSupplierId(),
                inboundTransaction.getSupplier().getSupplierName(),
                inboundTransaction.getQuantity(),
                inboundTransaction.getReceivedDate(),
                inboundTransaction.getReferenceNo(),
                inboundTransaction.getCreatedBy().getUserId(),
                inboundTransaction.getCreatedBy().getFullName(),
                inboundTransaction.getCreatedAt(),
                inboundTransaction.getStockAfterUpdate()
        );
    }
}