package com.esei.mei.tfm.MergeMarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.esei.mei.tfm.MergeMarket.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reanalyze/{categoryId}")
    public ResponseEntity<String> reanalyzePriceProducts(@PathVariable Long categoryId) {
        adminService.reanalyzePriceProducts(categoryId);
        return ResponseEntity.ok("Reanalysis of PriceProducts in category " + categoryId + " completed.");
    }
}