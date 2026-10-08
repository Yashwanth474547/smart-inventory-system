package com.smartinventory.impl;

import com.smartinventory.entity.Supplier;
import com.smartinventory.repository.SupplierRepository;
import com.smartinventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(id).orElse(null);

        if (existingSupplier != null) {
            existingSupplier.setSupplierName(supplier.getSupplierName());
            existingSupplier.setCompanyName(supplier.getCompanyName());
            existingSupplier.setEmail(supplier.getEmail());
            existingSupplier.setPhone(supplier.getPhone());
            existingSupplier.setAddress(supplier.getAddress());
            existingSupplier.setCity(supplier.getCity());
            existingSupplier.setState(supplier.getState());
            existingSupplier.setCountry(supplier.getCountry());

            return supplierRepository.save(existingSupplier);
        }

        return null;
    }

    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}