package com.esei.mei.tfm.MergeMarket.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.service.PriceProductService;
import com.esei.mei.tfm.MergeMarket.service.ProductService;

@RestController
@RequestMapping("/api/priceproducts")
@CrossOrigin(origins = "http://84.246.213.41:8081")
public class PriceProductController {

    @Autowired
    private PriceProductService priceProductService;
    
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<PriceProduct> createPriceProduct(@RequestBody PriceProduct priceProduct) {
        PriceProduct createdPriceProduct = priceProductService.create(priceProduct);
        return new ResponseEntity<>(createdPriceProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{priceProductId}")
    public ResponseEntity<PriceProduct> updatePriceProduct(@PathVariable Long priceProductId, @RequestBody PriceProduct priceProduct) {
        priceProduct.setIdPrice(priceProductId);
        PriceProduct updatedPriceProduct = priceProductService.update(priceProduct);
        return new ResponseEntity<>(updatedPriceProduct, HttpStatus.OK);
    }

    @GetMapping("/bycategory/{categoryId}")
    public ResponseEntity<List<PriceProduct>> findPriceProductsByCategory(@PathVariable ProductCategory categoryId) {
        List<PriceProduct> priceProducts = priceProductService.findByCategory(categoryId);
        return new ResponseEntity<>(priceProducts, HttpStatus.OK);
    }
    
    @GetMapping("/withoutProductId")
    public ResponseEntity<List<PriceProduct>> findPriceProductsWithoutProductId() {
        List<PriceProduct> priceProductsWithoutProductId = priceProductService.findPriceProductsWithoutProductId();
        return new ResponseEntity<>(priceProductsWithoutProductId, HttpStatus.OK);
    }

    @GetMapping("/byproduct/{productId}")
    public ResponseEntity<List<PriceProduct>> findPriceProductsByProductId(@PathVariable Long productId) {
    	Optional<Product> optionalproduct = productService.findById(productId);
    	Product product = null;
    	if (optionalproduct.isPresent()) {
            product = optionalproduct.get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    	List<PriceProduct> priceProducts = priceProductService.findByProductId(product);
        return new ResponseEntity<>(priceProducts, HttpStatus.OK);
    }

}
