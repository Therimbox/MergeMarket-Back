package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import com.esei.mei.tfm.MergeMarket.entity.ProductGroup;

public interface ProductGroupService {
    void initializeProductGroup();

	List<ProductGroup> findAll();
}
