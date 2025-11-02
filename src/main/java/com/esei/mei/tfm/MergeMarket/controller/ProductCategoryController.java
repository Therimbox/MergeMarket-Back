package com.esei.mei.tfm.MergeMarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.service.ProductCategoryService;

@RestController
@RequestMapping("/api/productcategories")
@CrossOrigin(origins = "*")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping
    public ResponseEntity<ProductCategory> createProductCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory createdProductCategory = productCategoryService.create(productCategory);
        return new ResponseEntity<>(createdProductCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ProductCategory> getProductCategoryById(@PathVariable Long categoryId) {
        ProductCategory productCategory = productCategoryService.findById(categoryId);
        if (productCategory != null) {
            return new ResponseEntity<>(productCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllProductCategories() {
        List<ProductCategory> productCategories = productCategoryService.findAll();
        return new ResponseEntity<>(productCategories, HttpStatus.OK);
    }

}
