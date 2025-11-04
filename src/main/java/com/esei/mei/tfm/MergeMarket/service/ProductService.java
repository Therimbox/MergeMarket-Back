package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;
import java.util.Optional;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface ProductService {

	Product create(Product product);

	Product update(Product product);

	List<Product> findByName(String name);

	List<Product> findByCategory(ProductCategory category);

	Optional<Product> findById(Long id);

	List<Product> findByKeyword(String keyword);

	List<Product> findProduct(String name, ProductCategory category);

	ProductCategory findCategoryById(Long id);

	Product findByNameAndCategory(String name, ProductCategory category);

	Product findMatchingProduct(String productName, ProductCategory category);

}
