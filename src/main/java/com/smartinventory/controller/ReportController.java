package com.smartinventory.controller;

import com.smartinventory.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;


    // ==========================================
    // SUMMARY REPORT
    // ==========================================

    @GetMapping("/summary")
    public Map<String, Object> getSummaryReport() {

        return reportService.getSummaryReport();
    }


    // ==========================================
    // SALES REPORT
    // ==========================================

    @GetMapping("/sales")
    public Map<String, Object> getSalesReport() {

        return reportService.getSalesReport();
    }


    // ==========================================
    // PURCHASE REPORT
    // ==========================================

    @GetMapping("/purchases")
    public Map<String, Object> getPurchaseReport() {

        return reportService.getPurchaseReport();
    }
}