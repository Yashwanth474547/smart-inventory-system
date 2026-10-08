package com.smartinventory.service;

import com.smartinventory.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {

    // Create Purchase Order
    PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder);

    // Get All Purchase Orders
    List<PurchaseOrder> getAllPurchaseOrders();

    // Get Purchase Order By Id
    PurchaseOrder getPurchaseOrderById(Long id);

    // Update Purchase Order
    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrder);

    // Delete Purchase Order
    void deletePurchaseOrder(Long id);

    // Approve Purchase Order
    PurchaseOrder approvePurchaseOrder(Long id);
}