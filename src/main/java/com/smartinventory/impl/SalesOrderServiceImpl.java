package com.smartinventory.impl;

import com.smartinventory.entity.Inventory;
import com.smartinventory.entity.Product;
import com.smartinventory.entity.SalesOrder;

import com.smartinventory.repository.InventoryRepository;
import com.smartinventory.repository.ProductRepository;
import com.smartinventory.repository.SalesOrderRepository;

import com.smartinventory.service.SalesOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;


    // ==========================================
    // CREATE SALES ORDER
    // ==========================================

    @Override
    @Transactional
    public SalesOrder saveSalesOrder(
            SalesOrder salesOrder) {

        // Check Product
        if (salesOrder.getProduct() == null ||
                salesOrder.getProduct().getProductId() == null) {

            throw new RuntimeException(
                    "Product is required"
            );
        }


        // Check Quantity
        if (salesOrder.getQuantity() == null ||
                salesOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Sales quantity must be greater than zero"
            );
        }


        // Check Unit Price
        if (salesOrder.getUnitPrice() == null ||
                salesOrder.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than zero"
            );
        }


        // Find actual Product
        Product product =
                productRepository
                        .findById(
                                salesOrder
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );


        // Set actual Product entity
        salesOrder.setProduct(product);


        // ==========================================
        // IMPORTANT:
        // KEEP THE UNIT PRICE ENTERED BY USER
        // ==========================================

        // Example:
        //
        // Product Price = ₹50,000
        // Sales Unit Price = ₹45,000
        //
        // The system keeps ₹45,000.


        // Calculate Total Amount
        salesOrder.setTotalAmount(
                salesOrder.getUnitPrice()
                        * salesOrder.getQuantity()
        );


        // Default Status
        if (salesOrder.getStatus() == null ||
                salesOrder.getStatus().isBlank()) {

            salesOrder.setStatus("PENDING");
        }


        return salesOrderRepository.save(
                salesOrder
        );
    }


    // ==========================================
    // GET ALL SALES ORDERS
    // ==========================================

    @Override
    public List<SalesOrder> getAllSalesOrders() {

        return salesOrderRepository.findAll();
    }


    // ==========================================
    // GET SALES ORDER BY ID
    // ==========================================

    @Override
    public SalesOrder getSalesOrderById(
            Long id) {

        return salesOrderRepository
                .findById(id)
                .orElse(null);
    }


    // ==========================================
    // UPDATE SALES ORDER
    // ==========================================

    @Override
    @Transactional
    public SalesOrder updateSalesOrder(
            Long id,
            SalesOrder salesOrder) {

        SalesOrder existing =
                salesOrderRepository
                        .findById(id)
                        .orElse(null);


        if (existing == null) {
            return null;
        }


        // Do not allow modification
        // after approval
        if ("APPROVED".equalsIgnoreCase(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Approved Sales Order cannot be modified"
            );
        }


        // Check Product
        if (salesOrder.getProduct() == null ||
                salesOrder.getProduct().getProductId() == null) {

            throw new RuntimeException(
                    "Product is required"
            );
        }


        // Check Quantity
        if (salesOrder.getQuantity() == null ||
                salesOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Sales quantity must be greater than zero"
            );
        }


        // Check Unit Price
        if (salesOrder.getUnitPrice() == null ||
                salesOrder.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than zero"
            );
        }


        // Find Product
        Product product =
                productRepository
                        .findById(
                                salesOrder
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );


        // ==========================================
        // UPDATE FIELDS
        // ==========================================

        existing.setProduct(product);

        existing.setQuantity(
                salesOrder.getQuantity()
        );

        existing.setCustomerName(
                salesOrder.getCustomerName()
        );

        existing.setCustomerPhone(
                salesOrder.getCustomerPhone()
        );

        existing.setCustomerEmail(
                salesOrder.getCustomerEmail()
        );

        existing.setCustomerAddress(
                salesOrder.getCustomerAddress()
        );

        existing.setOrderDate(
                salesOrder.getOrderDate()
        );


        // ==========================================
        // UPDATE STATUS
        // ==========================================

        if (salesOrder.getStatus() != null &&
                !salesOrder.getStatus().isBlank()) {

            existing.setStatus(
                    salesOrder.getStatus()
            );
        }


        // ==========================================
        // IMPORTANT:
        // KEEP USER ENTERED UNIT PRICE
        // ==========================================

        existing.setUnitPrice(
                salesOrder.getUnitPrice()
        );


        // ==========================================
        // CALCULATE TOTAL
        // ==========================================

        existing.setTotalAmount(
                salesOrder.getUnitPrice()
                        * salesOrder.getQuantity()
        );


        return salesOrderRepository.save(
                existing
        );
    }


    // ==========================================
    // DELETE SALES ORDER
    // ==========================================

    @Override
    @Transactional
    public void deleteSalesOrder(Long id) {

        SalesOrder existing =
                salesOrderRepository
                        .findById(id)
                        .orElse(null);


        if (existing == null) {

            throw new RuntimeException(
                    "Sales Order not found"
            );
        }


        // Prevent deleting approved order
        // because inventory has already changed
        if ("APPROVED".equalsIgnoreCase(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Approved Sales Order cannot be deleted"
            );
        }


        salesOrderRepository.deleteById(id);
    }


    // ==========================================
    // APPROVE SALES ORDER
    // ==========================================

    @Override
    @Transactional
    public SalesOrder approveSalesOrder(
            Long id) {

        // ======================================
        // FIND SALES ORDER
        // ======================================

        SalesOrder salesOrder =
                salesOrderRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Sales Order not found"
                                )
                        );


        // ======================================
        // PREVENT DUPLICATE APPROVAL
        // ======================================

        if ("APPROVED".equalsIgnoreCase(
                salesOrder.getStatus())) {

            return salesOrder;
        }


        // ======================================
        // VALIDATE PRODUCT
        // ======================================

        if (salesOrder.getProduct() == null ||
                salesOrder.getProduct().getProductId() == null) {

            throw new RuntimeException(
                    "Product not found in Sales Order"
            );
        }


        // ======================================
        // VALIDATE QUANTITY
        // ======================================

        if (salesOrder.getQuantity() == null ||
                salesOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Sales quantity must be greater than zero"
            );
        }


        // ======================================
        // VALIDATE UNIT PRICE
        // ======================================

        if (salesOrder.getUnitPrice() == null ||
                salesOrder.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than zero"
            );
        }


        int requestedQuantity =
                salesOrder.getQuantity();


        // ======================================
        // FIND PRODUCT
        // ======================================

        Product product =
                productRepository
                        .findById(
                                salesOrder
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product not found"
                                )
                        );


        // ======================================
        // SET ACTUAL PRODUCT
        // ======================================

        salesOrder.setProduct(product);


        // ======================================
        // FIND INVENTORY
        // ======================================

        Inventory inventory =
                inventoryRepository
                        .findByProduct(product)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Inventory not found for product: "
                                                + product.getProductName()
                                )
                        );


        // ======================================
        // GET CURRENT STOCK
        // ======================================

        int availableStock =
                inventory.getQuantity() == null
                        ? 0
                        : inventory.getQuantity();


        // ======================================
        // CHECK STOCK
        // ======================================

        if (availableStock < requestedQuantity) {

            throw new RuntimeException(
                    "Insufficient Stock. Available: "
                            + availableStock
                            + ", Requested: "
                            + requestedQuantity
            );
        }


        // ======================================
        // REDUCE INVENTORY
        // ======================================

        int remainingStock =
                availableStock
                        - requestedQuantity;


        inventory.setQuantity(
                remainingStock
        );


        // ======================================
        // SAVE INVENTORY
        // ======================================

        inventoryRepository.save(
                inventory
        );


        // ======================================
        // UPDATE SALES ORDER
        // ======================================

        // IMPORTANT:
        // Keep the Unit Price entered by user.

        salesOrder.setUnitPrice(
                salesOrder.getUnitPrice()
        );


        // Calculate total using
        // entered Unit Price.

        salesOrder.setTotalAmount(
                salesOrder.getUnitPrice()
                        * requestedQuantity
        );


        // ======================================
        // UPDATE STATUS
        // ======================================

        salesOrder.setStatus(
                "APPROVED"
        );


        // ======================================
        // SAVE SALES ORDER
        // ======================================

        return salesOrderRepository.save(
                salesOrder
        );
    }
}