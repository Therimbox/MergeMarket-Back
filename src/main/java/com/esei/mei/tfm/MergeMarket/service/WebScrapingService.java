package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface WebScrapingService {

	List<Product> scrapeAndSaveProducts(String baseUrl, ProductCategory category);

	void insertProducts(ProductCategory category);

	void assignProductsToPriceProducts();
}
