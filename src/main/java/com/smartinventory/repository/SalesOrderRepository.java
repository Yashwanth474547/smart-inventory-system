package com.smartinventory.repository;

import com.smartinventory.entity.SalesOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalesOrderRepository
        extends JpaRepository<SalesOrder, Long> {

    @Query("""
        SELECT COALESCE(SUM(s.totalAmount), 0)
        FROM SalesOrder s
        WHERE UPPER(s.status) = 'APPROVED'
    """)
    Double getTotalSalesValue();


    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM SalesOrder s
        WHERE s.product.productId = :productId
        AND s.orderDate >= :startDate
        AND UPPER(s.status) = 'APPROVED'
    """)
    Integer getTotalQuantitySold(
            @Param("productId") Long productId,
            @Param("startDate") LocalDate startDate
    );


    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM SalesOrder s
        WHERE UPPER(s.status) = 'APPROVED'
    """)
    Integer getTotalQuantitySoldAll();


    // =========================================================
    // BEST-SELLING PRODUCTS
    // =========================================================

    @Query("""
        SELECT s.product.productName, SUM(s.quantity)
        FROM SalesOrder s
        WHERE UPPER(s.status) = 'APPROVED'
        GROUP BY s.product.productId, s.product.productName
        ORDER BY SUM(s.quantity) DESC
    """)
    List<Object[]> getBestSellingProducts();


    // =========================================================
    // SALES PERFORMANCE
    // =========================================================

    @Query("""
        SELECT s.product.productName, SUM(s.quantity)
        FROM SalesOrder s
        WHERE UPPER(s.status) = 'APPROVED'
        GROUP BY s.product.productId, s.product.productName
        ORDER BY SUM(s.quantity) DESC
    """)
    List<Object[]> getSalesPerformance();
}