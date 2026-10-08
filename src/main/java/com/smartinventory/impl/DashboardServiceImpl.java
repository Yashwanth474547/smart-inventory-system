package com.smartinventory.impl;

import com.smartinventory.repository.InventoryRepository;
import com.smartinventory.repository.ProductRepository;
import com.smartinventory.repository.PurchaseOrderRepository;
import com.smartinventory.repository.SalesOrderRepository;
import com.smartinventory.repository.SupplierRepository;
import com.smartinventory.repository.WarehouseRepository;
import com.smartinventory.service.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Override
    public Map<String, Object> getDashboardData() {

        Map<String, Object> dashboard = new HashMap<>();

        Integer totalStock = inventoryRepository.getTotalStock();

        dashboard.put("totalProducts", productRepository.count());
        dashboard.put("totalSuppliers", supplierRepository.count());
        dashboard.put("totalStock", totalStock == null ? 0 : totalStock);
        dashboard.put("totalWarehouses", warehouseRepository.count());
        dashboard.put("totalPurchaseOrders", purchaseOrderRepository.count());
        dashboard.put("totalSalesOrders", salesOrderRepository.count());

        // Low Stock Count
        dashboard.put("lowStockProducts",
                inventoryRepository.getLowStockProducts().size());

        return dashboard;
    }
}