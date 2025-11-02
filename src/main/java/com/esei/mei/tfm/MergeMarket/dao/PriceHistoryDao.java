package com.esei.mei.tfm.MergeMarket.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esei.mei.tfm.MergeMarket.entity.PriceHistory;

public interface PriceHistoryDao extends JpaRepository<PriceHistory, Long>{

	List<PriceHistory> findByProductIdProduct(Long productId);
}
