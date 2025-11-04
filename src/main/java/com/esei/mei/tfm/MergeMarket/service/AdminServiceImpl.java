package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private PriceProductService priceProductService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Override
    public void reanalyzePriceProducts(Long categoryId) {
        ProductCategory category = productCategoryService.findById(categoryId);
        Product otherProduct = productService.findByName("otro").stream().filter(p -> p.getCategory().equals(category)).findFirst().orElseThrow(() -> new IllegalArgumentException("Product 'otro' not found in category"));

        List<PriceProduct> priceProducts = priceProductService.findByProduct(otherProduct);

        for (PriceProduct priceProduct : priceProducts) {
            Product matchingProduct = productService.findProduct(priceProduct.getName(), category).stream().findFirst().orElse(null);
            if (matchingProduct != null) {
                priceProduct.setProduct(matchingProduct);
                priceProductService.update(priceProduct);
            }
        }
    }
}