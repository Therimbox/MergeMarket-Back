package com.esei.mei.tfm.MergeMarket.service;

import java.util.Date;
import java.util.List;

import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface PriceProductService {

    PriceProduct create(PriceProduct priceProduct);

    PriceProduct update(PriceProduct priceProduct);

    List<PriceProduct> findByCategory(ProductCategory category);

    List<PriceProduct> findPriceProductsWithoutProductId();

    List<PriceProduct> findByProductId(Product productId);
    
    List<PriceProduct> findByProductAndUrl(Product product, String url);
    
    PriceProduct createOrUpdatePriceProduct(String name, double price, String url, Date lastDate, ProductCategory category, String image);

	List<PriceProduct> findByProductAndUrlContaining(Product product, String url);

	void deletePriceProducts(String baseUrl, ProductCategory category);
}
