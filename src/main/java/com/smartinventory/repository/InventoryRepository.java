package com.smartinventory.repository;

import com.smartinventory.entity.Inventory;
import com.smartinventory.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    @Query("SELECT SUM(i.quantity) FROM Inventory i")
    Integer getTotalStock();

    @Query("SELECT SUM(i.quantity * i.product.price) FROM Inventory i")
    Double getTotalStockValue();

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> getLowStockProducts();
}