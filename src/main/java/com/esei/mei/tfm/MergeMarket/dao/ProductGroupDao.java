package com.esei.mei.tfm.MergeMarket.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esei.mei.tfm.MergeMarket.entity.ProductGroup;

public interface ProductGroupDao extends JpaRepository<ProductGroup, Long>{

	ProductGroup findByName(String name);
}
