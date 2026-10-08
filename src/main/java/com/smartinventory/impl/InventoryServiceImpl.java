package com.smartinventory.impl;

import com.smartinventory.entity.Inventory;
import com.smartinventory.repository.InventoryRepository;
import com.smartinventory.repository.SalesOrderRepository;
import com.smartinventory.service.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;


    // ==========================
    // SAVE INVENTORY
    // ==========================

    @Override
    @Transactional
    public Inventory saveInventory(Inventory inventory) {

        if (inventory == null) {
            throw new RuntimeException("Inventory cannot be null");
        }

        if (inventory.getProduct() == null) {
            throw new RuntimeException("Product is required");
        }

        if (inventory.getWarehouse() == null) {
            throw new RuntimeException("Warehouse is required");
        }

        if (inventory.getQuantity() == null) {
            inventory.setQuantity(0);
        }

        if (inventory.getReorderLevel() == null) {
            inventory.setReorderLevel(0);
        }

        return inventoryRepository.save(inventory);
    }


    // ==========================
    // GET ALL INVENTORIES
    // ==========================

    @Override
    public List<Inventory> getAllInventories() {

        return inventoryRepository.findAll();
    }


    // ==========================
    // GET INVENTORY BY ID
    // ==========================

    @Override
    public Inventory getInventoryById(Long id) {

        return inventoryRepository
                .findById(id)
                .orElse(null);
    }


    // ==========================
    // UPDATE INVENTORY
    // ==========================

    @Override
    @Transactional
    public Inventory updateInventory(
            Long id,
            Inventory inventory) {

        Inventory existing =
                inventoryRepository
                        .findById(id)
                        .orElse(null);

        if (existing == null) {
            return null;
        }


        if (inventory.getProduct() != null) {
            existing.setProduct(
                    inventory.getProduct()
            );
        }


        if (inventory.getWarehouse() != null) {
            existing.setWarehouse(
                    inventory.getWarehouse()
            );
        }


        if (inventory.getQuantity() != null) {
            existing.setQuantity(
                    inventory.getQuantity()
            );
        }


        if (inventory.getReorderLevel() != null) {
            existing.setReorderLevel(
                    inventory.getReorderLevel()
            );
        }


        if (inventory.getLastUpdated() != null) {
            existing.setLastUpdated(
                    inventory.getLastUpdated()
            );
        }


        return inventoryRepository.save(existing);
    }


    // ==========================
    // DELETE INVENTORY
    // ==========================

    @Override
    @Transactional
    public void deleteInventory(Long id) {

        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException(
                    "Inventory not found with id: " + id
            );
        }

        inventoryRepository.deleteById(id);
    }


    // ==========================
    // LOW STOCK PRODUCTS
    // ==========================

    @Override
    public List<Inventory> getLowStockProducts() {

        return inventoryRepository
                .getLowStockProducts();
    }


    // ==========================
    // SMART REORDER RECOMMENDATION
    // ==========================

    @Override
    public Integer getRecommendedOrderQuantity(
            Long inventoryId) {

        Inventory inventory =
                inventoryRepository
                        .findById(inventoryId)
                        .orElse(null);


        // --------------------------
        // INVENTORY VALIDATION
        // --------------------------

        if (inventory == null) {
            return 0;
        }


        if (inventory.getProduct() == null) {
            return 0;
        }


        int currentStock =
                inventory.getQuantity() != null
                        ? inventory.getQuantity()
                        : 0;


        int reorderLevel =
                inventory.getReorderLevel() != null
                        ? inventory.getReorderLevel()
                        : 0;


        Long productId =
                inventory
                        .getProduct()
                        .getProductId();


        // --------------------------
        // LAST 30 DAYS
        // SALES
        // --------------------------

        LocalDate startDate =
                LocalDate.now()
                        .minusDays(30);


        Integer totalSold =
                salesOrderRepository
                        .getTotalQuantitySold(
                                productId,
                                startDate
                        );


        if (totalSold == null) {
            totalSold = 0;
        }


        // --------------------------
        // AVERAGE DAILY SALES
        // --------------------------

        double averageDailySales =
                totalSold / 30.0;


        // --------------------------
        // TARGET STOCK
        //
        // Keep at least:
        //
        // 1. Reorder Level
        // 2. 30 days of expected sales
        // --------------------------

        int salesBasedTarget =
                (int) Math.ceil(
                        averageDailySales * 30
                );


        int targetStock =
                Math.max(
                        reorderLevel,
                        salesBasedTarget
                );


        // --------------------------
        // RECOMMENDED ORDER
        // --------------------------

        int recommendedQuantity =
                targetStock - currentStock;


        // --------------------------
        // NEVER RETURN NEGATIVE
        // --------------------------

        return Math.max(
                recommendedQuantity,
                0
        );
    }
}