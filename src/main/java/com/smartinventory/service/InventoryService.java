package com.smartinventory.service;

import com.smartinventory.entity.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory saveInventory(Inventory inventory);

    List<Inventory> getAllInventories();

    Inventory getInventoryById(Long id);

    Inventory updateInventory(
            Long id,
            Inventory inventory
    );

    void deleteInventory(Long id);

    // ==========================
    // LOW STOCK PRODUCTS
    // ==========================

    List<Inventory> getLowStockProducts();

    // ==========================
    // REORDER RECOMMENDATION
    // ==========================

    Integer getRecommendedOrderQuantity(
            Long inventoryId
    );
}