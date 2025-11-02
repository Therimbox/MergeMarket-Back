package com.esei.mei.tfm.MergeMarket.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface ProductDao extends JpaRepository<Product, Long>{
	
	List<Product> findByName(String name);
	
	List<Product> findByCategory(ProductCategory category);
	
	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Product> findByKeyword(@Param("keyword") String keyword);
}
