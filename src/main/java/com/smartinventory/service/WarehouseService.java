package com.smartinventory.service;

import com.smartinventory.entity.Warehouse;

import java.util.List;

public interface WarehouseService {

    Warehouse saveWarehouse(Warehouse warehouse);

    List<Warehouse> getAllWarehouses();

    Warehouse getWarehouseById(Long id);

    Warehouse updateWarehouse(Long id, Warehouse warehouse);

    void deleteWarehouse(Long id);
}