package com.smartinventory.controller;

import com.smartinventory.entity.SalesOrder;
import com.smartinventory.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salesorders")
@CrossOrigin("*")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    // ==========================
    // CREATE SALES ORDER
    // ==========================
    @PostMapping
    public SalesOrder saveSalesOrder(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.saveSalesOrder(salesOrder);
    }

    // ==========================
    // GET ALL SALES ORDERS
    // ==========================
    @GetMapping
    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    // ==========================
    // GET SALES ORDER BY ID
    // ==========================
    @GetMapping("/{id}")
    public SalesOrder getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }

    // ==========================
    // UPDATE SALES ORDER
    // ==========================
    @PutMapping("/{id}")
    public SalesOrder updateSalesOrder(@PathVariable Long id,
                                       @RequestBody SalesOrder salesOrder) {
        return salesOrderService.updateSalesOrder(id, salesOrder);
    }

    // ==========================
    // DELETE SALES ORDER
    // ==========================
    @DeleteMapping("/{id}")
    public void deleteSalesOrder(@PathVariable Long id) {
        salesOrderService.deleteSalesOrder(id);
    }

    // ==========================
    // APPROVE SALES ORDER
    // ==========================
    @PutMapping("/{id}/approve")
    public SalesOrder approveSalesOrder(@PathVariable Long id) {
        return salesOrderService.approveSalesOrder(id);
    }
}