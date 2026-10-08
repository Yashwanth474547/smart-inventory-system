package com.smartinventory.repository;

import com.smartinventory.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    // =====================================================
    // TOTAL PURCHASE VALUE
    // Only APPROVED purchase orders
    // =====================================================

    @Query("""
        SELECT COALESCE(SUM(p.totalAmount), 0)
        FROM PurchaseOrder p
        WHERE UPPER(p.status) = 'APPROVED'
    """)
    Double getTotalPurchaseValue();


    // =====================================================
    // FIND APPROVED SUPPLIER PRICES
    // Used by AI Purchase Plan
    // =====================================================

    @Query("""
        SELECT p
        FROM PurchaseOrder p
        WHERE p.product.productId = :productId
        AND UPPER(p.status) = 'APPROVED'
        AND p.unitPrice IS NOT NULL
        ORDER BY p.unitPrice ASC
    """)
    List<PurchaseOrder> findApprovedSupplierPrices(
            Long productId
    );
}