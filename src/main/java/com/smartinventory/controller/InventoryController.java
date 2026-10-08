package com.smartinventory.controller;

import com.smartinventory.entity.Inventory;
import com.smartinventory.service.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@CrossOrigin("*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    // ==========================
    // SAVE INVENTORY
    // ==========================

    @PostMapping
    public Inventory saveInventory(
            @RequestBody Inventory inventory) {

        return inventoryService.saveInventory(inventory);
    }


    // ==========================
    // GET ALL INVENTORIES
    // ==========================

    @GetMapping
    public List<Inventory> getAllInventories() {

        return inventoryService.getAllInventories();
    }


    // ==========================
    // LOW STOCK PRODUCTS
    // IMPORTANT: BEFORE /{id}
    // ==========================

    @GetMapping("/low-stock")
    public List<Inventory> getLowStockProducts() {

        return inventoryService.getLowStockProducts();
    }


    // ==========================
    // REORDER RECOMMENDATION
    // ==========================

    @GetMapping("/{id}/reorder-recommendation")
    public Integer getRecommendedOrderQuantity(
            @PathVariable Long id) {

        return inventoryService
                .getRecommendedOrderQuantity(id);
    }


    // ==========================
    // GET INVENTORY BY ID
    // ==========================

    @GetMapping("/{id}")
    public Inventory getInventoryById(
            @PathVariable Long id) {

        return inventoryService
                .getInventoryById(id);
    }


    // ==========================
    // UPDATE INVENTORY
    // ==========================

    @PutMapping("/{id}")
    public Inventory updateInventory(
            @PathVariable Long id,
            @RequestBody Inventory inventory) {

        return inventoryService
                .updateInventory(id, inventory);
    }


    // ==========================
    // DELETE INVENTORY
    // ==========================

    @DeleteMapping("/{id}")
    public String deleteInventory(
            @PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return "Inventory deleted successfully";
    }
}