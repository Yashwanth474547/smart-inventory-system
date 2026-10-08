package com.smartinventory.service;

import com.smartinventory.entity.SalesOrder;

import java.util.List;

public interface SalesOrderService {

    SalesOrder saveSalesOrder(SalesOrder salesOrder);

    List<SalesOrder> getAllSalesOrders();

    SalesOrder getSalesOrderById(Long id);

    SalesOrder updateSalesOrder(Long id, SalesOrder salesOrder);

    void deleteSalesOrder(Long id);

    // NEW
    SalesOrder approveSalesOrder(Long id);
}