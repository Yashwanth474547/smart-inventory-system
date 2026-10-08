package com.smartinventory.impl;

import com.smartinventory.entity.Warehouse;
import com.smartinventory.repository.WarehouseRepository;
import com.smartinventory.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    public Warehouse saveWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElse(null);
    }

    @Override
    public Warehouse updateWarehouse(Long id, Warehouse warehouse) {
        Warehouse existing = warehouseRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setWarehouseName(warehouse.getWarehouseName());
            existing.setLocation(warehouse.getLocation());
            existing.setCity(warehouse.getCity());
            existing.setState(warehouse.getState());
            existing.setCapacity(warehouse.getCapacity());

            return warehouseRepository.save(existing);
        }

        return null;
    }

    @Override
    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }
}