package com.esei.mei.tfm.MergeMarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esei.mei.tfm.MergeMarket.entity.PriceHistory;
import com.esei.mei.tfm.MergeMarket.service.PriceHistoryService;

@RestController
@RequestMapping("/api/priceHistory")
@CrossOrigin(origins = "http://84.246.213.41:8081")
public class PriceHistoryController {
	
	@Autowired
    private PriceHistoryService priceHistoryService;

	@GetMapping("/byProductId/{productId}")
    public ResponseEntity<List<PriceHistory>> findByProductId(@PathVariable Long productId) {
        List<PriceHistory> priceHistories = priceHistoryService.findByProductIdProduct(productId);
        return new ResponseEntity<>(priceHistories, HttpStatus.OK);
    }
}
