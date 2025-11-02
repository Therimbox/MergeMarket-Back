package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface ProductCategoryService {

	void initializeCategories();
	
	ProductCategory create(ProductCategory productCategory);

	ProductCategory findById(Long id);

	List<ProductCategory> findAll();
}
