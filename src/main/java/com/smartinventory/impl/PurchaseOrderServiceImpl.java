package com.smartinventory.impl;

import com.smartinventory.entity.Inventory;
import com.smartinventory.entity.Product;
import com.smartinventory.entity.PurchaseOrder;
import com.smartinventory.entity.Supplier;

import com.smartinventory.repository.InventoryRepository;
import com.smartinventory.repository.ProductRepository;
import com.smartinventory.repository.PurchaseOrderRepository;
import com.smartinventory.repository.SupplierRepository;

import com.smartinventory.service.PurchaseOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InventoryRepository inventoryRepository;


    // ==========================================
    // CREATE PURCHASE ORDER
    // ==========================================

    @Override
    public PurchaseOrder savePurchaseOrder(
            PurchaseOrder purchaseOrder) {

        // Check Product
        if (purchaseOrder.getProduct() == null ||
                purchaseOrder.getProduct().getProductId() == null) {

            throw new RuntimeException("Product is required");
        }


        // Check Supplier
        if (purchaseOrder.getSupplier() == null ||
                purchaseOrder.getSupplier().getSupplierId() == null) {

            throw new RuntimeException("Supplier is required");
        }


        // Check Quantity
        if (purchaseOrder.getQuantity() == null ||
                purchaseOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }


        // Check Unit Price
        if (purchaseOrder.getUnitPrice() == null ||
                purchaseOrder.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than 0"
            );
        }


        // Find actual Product
        Product product =
                productRepository.findById(
                                purchaseOrder
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );


        // Find actual Supplier
        Supplier supplier =
                supplierRepository.findById(
                                purchaseOrder
                                        .getSupplier()
                                        .getSupplierId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Supplier not found"
                                )
                        );


        // Set actual entities
        purchaseOrder.setProduct(product);
        purchaseOrder.setSupplier(supplier);


        // IMPORTANT:
        // Keep the Unit Price entered by the user.
        //
        // Example:
        // Product price = 50000
        // Purchase unit price = 45000
        //
        // We DO NOT replace 45000 with 50000.


        // Calculate Total Amount
        purchaseOrder.setTotalAmount(
                purchaseOrder.getUnitPrice()
                        * purchaseOrder.getQuantity()
        );


        // Default Status
        if (purchaseOrder.getStatus() == null ||
                purchaseOrder.getStatus().isBlank()) {

            purchaseOrder.setStatus("PENDING");
        }


        return purchaseOrderRepository.save(
                purchaseOrder
        );
    }


    // ==========================================
    // GET ALL PURCHASE ORDERS
    // ==========================================

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {

        return purchaseOrderRepository.findAll();
    }


    // ==========================================
    // GET PURCHASE ORDER BY ID
    // ==========================================

    @Override
    public PurchaseOrder getPurchaseOrderById(
            Long id) {

        return purchaseOrderRepository
                .findById(id)
                .orElse(null);
    }


    // ==========================================
    // UPDATE PURCHASE ORDER
    // ==========================================

    @Override
    public PurchaseOrder updatePurchaseOrder(
            Long id,
            PurchaseOrder purchaseOrder) {

        PurchaseOrder existing =
                purchaseOrderRepository
                        .findById(id)
                        .orElse(null);


        if (existing == null) {
            return null;
        }


        // Do not allow editing delivered order
        if ("DELIVERED".equalsIgnoreCase(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Delivered Purchase Order cannot be edited"
            );
        }


        // Check Product
        if (purchaseOrder.getProduct() == null ||
                purchaseOrder.getProduct().getProductId() == null) {

            throw new RuntimeException(
                    "Product is required"
            );
        }


        // Check Supplier
        if (purchaseOrder.getSupplier() == null ||
                purchaseOrder.getSupplier().getSupplierId() == null) {

            throw new RuntimeException(
                    "Supplier is required"
            );
        }


        // Check Quantity
        if (purchaseOrder.getQuantity() == null ||
                purchaseOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }


        // Check Unit Price
        if (purchaseOrder.getUnitPrice() == null ||
                purchaseOrder.getUnitPrice() <= 0) {

            throw new RuntimeException(
                    "Unit price must be greater than 0"
            );
        }


        // Find Product
        Product product =
                productRepository.findById(
                                purchaseOrder
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );


        // Find Supplier
        Supplier supplier =
                supplierRepository.findById(
                                purchaseOrder
                                        .getSupplier()
                                        .getSupplierId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Supplier not found"
                                )
                        );


        // ==========================================
        // UPDATE FIELDS
        // ==========================================

        existing.setProduct(product);

        existing.setSupplier(supplier);

        existing.setQuantity(
                purchaseOrder.getQuantity()
        );

        existing.setOrderDate(
                purchaseOrder.getOrderDate()
        );

        existing.setExpectedDeliveryDate(
                purchaseOrder
                        .getExpectedDeliveryDate()
        );


        // ==========================================
        // UPDATE STATUS
        // ==========================================

        if (purchaseOrder.getStatus() != null &&
                !purchaseOrder.getStatus().isBlank()) {

            existing.setStatus(
                    purchaseOrder.getStatus()
            );

        } else {

            existing.setStatus("PENDING");
        }


        // ==========================================
        // IMPORTANT:
        // KEEP USER ENTERED UNIT PRICE
        // ==========================================

        existing.setUnitPrice(
                purchaseOrder.getUnitPrice()
        );


        // ==========================================
        // CALCULATE TOTAL
        // ==========================================

        existing.setTotalAmount(
                purchaseOrder.getUnitPrice()
                        * purchaseOrder.getQuantity()
        );


        return purchaseOrderRepository.save(
                existing
        );
    }


    // ==========================================
    // DELETE PURCHASE ORDER
    // ==========================================

    @Override
    public void deletePurchaseOrder(Long id) {

        purchaseOrderRepository.deleteById(id);
    }


    // ==========================================
    // APPROVE / DELIVER PURCHASE ORDER
    // ==========================================

    @Override
    @Transactional
    public PurchaseOrder approvePurchaseOrder(
            Long id) {

        // ======================================
        // FIND PURCHASE ORDER
        // ======================================

        PurchaseOrder purchaseOrder =
                purchaseOrderRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Order not found"
                                )
                        );


        // ======================================
        // PREVENT DUPLICATE STOCK UPDATE
        // ======================================

        if ("DELIVERED".equalsIgnoreCase(
                purchaseOrder.getStatus())) {

            return purchaseOrder;
        }


        // ======================================
        // CHECK PRODUCT
        // ======================================

        Product product =
                purchaseOrder.getProduct();


        if (product == null ||
                product.getProductId() == null) {

            throw new RuntimeException(
                    "Product not found in Purchase Order"
            );
        }


        // ======================================
        // CHECK QUANTITY
        // ======================================

        if (purchaseOrder.getQuantity() == null ||
                purchaseOrder.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Invalid purchase quantity"
            );
        }


        // ======================================
        // FIND INVENTORY
        // ======================================

        Inventory inventory =
                inventoryRepository
                        .findByProduct(product)
                        .orElse(null);


        // ======================================
        // INVENTORY MUST EXIST
        // ======================================

        if (inventory == null) {

            throw new RuntimeException(
                    "Inventory not found for product: "
                            + product.getProductName()
            );
        }


        // ======================================
        // CURRENT STOCK
        // ======================================

        int currentQuantity =
                inventory.getQuantity() == null
                        ? 0
                        : inventory.getQuantity();


        // ======================================
        // PURCHASE QUANTITY
        // ======================================

        int purchaseQuantity =
                purchaseOrder.getQuantity();


        // ======================================
        // INCREASE INVENTORY
        // ======================================

        inventory.setQuantity(
                currentQuantity
                        + purchaseQuantity
        );


        // ======================================
        // SAVE INVENTORY
        // ======================================

        inventoryRepository.save(
                inventory
        );


        // ======================================
        // UPDATE PRODUCT QUANTITY
        // ======================================

        Integer productQuantity =
                product.getQuantity() == null
                        ? 0
                        : product.getQuantity();


        product.setQuantity(
                productQuantity
                        + purchaseQuantity
        );


        productRepository.save(product);


        // ======================================
        // UPDATE PURCHASE STATUS
        // ======================================

        purchaseOrder.setStatus(
                "DELIVERED"
        );


        // ======================================
        // SAVE PURCHASE ORDER
        // ======================================

        return purchaseOrderRepository.save(
                purchaseOrder
        );
    }
}