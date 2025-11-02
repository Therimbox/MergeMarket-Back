package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import com.esei.mei.tfm.MergeMarket.entity.PriceHistory;

public interface PriceHistoryService {

	PriceHistory create(PriceHistory priceHistory);

	PriceHistory update(PriceHistory priceHistory);

	List<PriceHistory> findByProductIdProduct(Long productId);
}
