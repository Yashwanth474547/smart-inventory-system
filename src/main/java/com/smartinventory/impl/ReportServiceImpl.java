package com.smartinventory.impl;

import com.smartinventory.entity.PurchaseOrder;
import com.smartinventory.entity.SalesOrder;
import com.smartinventory.entity.Product;

import com.smartinventory.repository.InventoryRepository;
import com.smartinventory.repository.PurchaseOrderRepository;
import com.smartinventory.repository.SalesOrderRepository;
import com.smartinventory.repository.ProductRepository;
import com.smartinventory.repository.SupplierRepository;
import com.smartinventory.repository.WarehouseRepository;

import com.smartinventory.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;


    // ==========================================
    // SUMMARY REPORT
    // ==========================================

    @Override
    public Map<String, Object> getSummaryReport() {

        Map<String, Object> report = new HashMap<>();


        // ======================================
        // TOTAL PURCHASE VALUE
        // Only APPROVED Purchase Orders
        // ======================================

        Double totalPurchase =
                purchaseOrderRepository.getTotalPurchaseValue();

        if (totalPurchase == null) {
            totalPurchase = 0.0;
        }


        // ======================================
        // TOTAL SALES VALUE
        // Only APPROVED Sales Orders
        // ======================================

        Double totalSales =
                salesOrderRepository.getTotalSalesValue();

        if (totalSales == null) {
            totalSales = 0.0;
        }


        // ======================================
        // TOTAL STOCK
        // ======================================

        Integer totalStock =
                inventoryRepository.getTotalStock();

        if (totalStock == null) {
            totalStock = 0;
        }


        // ======================================
        // CURRENT STOCK VALUE
        // ======================================

        Double stockValue =
                inventoryRepository.getTotalStockValue();

        if (stockValue == null) {
            stockValue = 0.0;
        }


        // ======================================
        // COUNTS
        // ======================================

        long totalProducts =
                productRepository.count();

        long totalSuppliers =
                supplierRepository.count();

        long totalWarehouses =
                warehouseRepository.count();

        long totalPurchaseOrders =
                purchaseOrderRepository.count();

        long totalSalesOrders =
                salesOrderRepository.count();


        // ======================================
        // COST OF GOODS SOLD
        // Uses Product COST PRICE
        // ======================================

        double costOfGoodsSold = 0.0;

        List<SalesOrder> salesOrders =
                salesOrderRepository.findAll();


        for (SalesOrder salesOrder : salesOrders) {

            // Only APPROVED sales affect COGS
            if ("APPROVED".equalsIgnoreCase(
                    salesOrder.getStatus())) {

                if (salesOrder.getQuantity() != null &&
                        salesOrder.getProduct() != null) {

                    Product product =
                            salesOrder.getProduct();

                    Double costPrice =
                            product.getCostPrice();

                    if (costPrice != null) {

                        costOfGoodsSold +=
                                salesOrder.getQuantity()
                                        * costPrice;
                    }
                }
            }
        }


        // ======================================
        // ESTIMATED PROFIT
        //
        // Profit = Sales Revenue - COGS
        // ======================================

        Double estimatedProfit =
                totalSales - costOfGoodsSold;


        // ======================================
        // ADD SUMMARY DATA
        // ======================================

        report.put(
                "totalPurchase",
                totalPurchase
        );

        report.put(
                "totalSales",
                totalSales
        );

        report.put(
                "totalStock",
                totalStock
        );

        report.put(
                "stockValue",
                stockValue
        );

        report.put(
                "costOfGoodsSold",
                costOfGoodsSold
        );

        report.put(
                "estimatedProfit",
                estimatedProfit
        );

        report.put(
                "totalProducts",
                totalProducts
        );

        report.put(
                "totalSuppliers",
                totalSuppliers
        );

        report.put(
                "totalWarehouses",
                totalWarehouses
        );

        report.put(
                "totalPurchaseOrders",
                totalPurchaseOrders
        );

        report.put(
                "totalSalesOrders",
                totalSalesOrders
        );


        return report;
    }


    // ==========================================
    // SALES REPORT
    // ==========================================

    @Override
    public Map<String, Object> getSalesReport() {

        Map<String, Object> report =
                new HashMap<>();


        List<SalesOrder> salesOrders =
                salesOrderRepository.findAll();


        double totalSales = 0.0;

        int totalQuantity = 0;

        int approvedOrders = 0;

        int pendingOrders = 0;


        for (SalesOrder order : salesOrders) {

            // Total quantity sold
            if (order.getQuantity() != null) {

                totalQuantity +=
                        order.getQuantity();
            }


            // Approved sales
            if ("APPROVED".equalsIgnoreCase(
                    order.getStatus())) {

                approvedOrders++;

                if (order.getTotalAmount() != null) {

                    totalSales +=
                            order.getTotalAmount();
                }

            } else {

                pendingOrders++;
            }
        }


        report.put(
                "totalSales",
                totalSales
        );

        report.put(
                "totalQuantitySold",
                totalQuantity
        );

        report.put(
                "approvedOrders",
                approvedOrders
        );

        report.put(
                "pendingOrders",
                pendingOrders
        );

        report.put(
                "totalOrders",
                salesOrders.size()
        );


        return report;
    }


    // ==========================================
    // PURCHASE REPORT
    // ==========================================

    @Override
    public Map<String, Object> getPurchaseReport() {

        Map<String, Object> report =
                new HashMap<>();


        List<PurchaseOrder> purchaseOrders =
                purchaseOrderRepository.findAll();


        double totalPurchase = 0.0;

        int totalQuantity = 0;

        int deliveredOrders = 0;

        int pendingOrders = 0;


        for (PurchaseOrder order : purchaseOrders) {

            // Total quantity purchased
            if (order.getQuantity() != null) {

                totalQuantity +=
                        order.getQuantity();
            }


            // Delivered purchase orders
            if ("DELIVERED".equalsIgnoreCase(
                    order.getStatus())) {

                deliveredOrders++;

                if (order.getTotalAmount() != null) {

                    totalPurchase +=
                            order.getTotalAmount();
                }

            } else {

                pendingOrders++;
            }
        }


        report.put(
                "totalPurchase",
                totalPurchase
        );

        report.put(
                "totalQuantityPurchased",
                totalQuantity
        );

        report.put(
                "deliveredOrders",
                deliveredOrders
        );

        report.put(
                "pendingOrders",
                pendingOrders
        );

        report.put(
                "totalOrders",
                purchaseOrders.size()
        );


        return report;
    }
}