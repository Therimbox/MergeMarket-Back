package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.dao.PriceHistoryDao;
import com.esei.mei.tfm.MergeMarket.entity.PriceHistory;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService{

	@Autowired
	private PriceHistoryDao priceHistoryDao;
	
	@Override
	public PriceHistory create(PriceHistory priceHistory) {
		return priceHistoryDao.save(priceHistory);
	}

	@Override
	public PriceHistory update(PriceHistory priceHistory) {
		if (priceHistory.getId() == null || !priceHistoryDao.existsById(priceHistory.getId())) {
            throw new IllegalArgumentException("El PriceHistory no existe en la base de datos.");
        }
        return priceHistoryDao.save(priceHistory);
	}

	@Override
	public List<PriceHistory> findByProductIdProduct(Long productId) {
		return priceHistoryDao.findByProductIdProduct(productId);
	}

	
}
