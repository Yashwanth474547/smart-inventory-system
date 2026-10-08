package com.smartinventory.service;

import com.smartinventory.entity.Supplier;

import java.util.List;

public interface SupplierService {

    Supplier saveSupplier(Supplier supplier);

    List<Supplier> getAllSuppliers();

    Supplier getSupplierById(Long id);

    Supplier updateSupplier(Long id, Supplier supplier);

    void deleteSupplier(Long id);
}