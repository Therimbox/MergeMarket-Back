package com.esei.mei.tfm.MergeMarket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://84.246.213.41:8081")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.create(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        product.setIdProduct(productId);
        Product updatedProduct = productService.update(product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/byname/{name}")
    public ResponseEntity<List<Product>> findProductsByName(@PathVariable String name) {
        List<Product> products = productService.findByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @GetMapping("/bycategory/{category}")
    public ResponseEntity<List<Product>> findProductsByCategory(@PathVariable ProductCategory category) {
        List<Product> products = productService.findByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
