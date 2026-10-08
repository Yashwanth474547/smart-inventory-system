package com.smartinventory.service;

import java.util.Map;

public interface ReportService {

    // Summary Dashboard
    Map<String, Object> getSummaryReport();

    // Sales Report
    Map<String, Object> getSalesReport();

    // Purchase Report
    Map<String, Object> getPurchaseReport();
}