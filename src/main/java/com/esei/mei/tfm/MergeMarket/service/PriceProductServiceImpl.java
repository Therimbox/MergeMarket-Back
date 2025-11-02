package com.esei.mei.tfm.MergeMarket.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.dao.PriceProductDao;
import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@Service
public class PriceProductServiceImpl implements PriceProductService {

	@Autowired
	private PriceProductDao priceProductDao;
	
	@Autowired
	private ProductService productService;

	@Override
	public PriceProduct create(PriceProduct priceProduct) {
		return priceProductDao.save(priceProduct);
	}

	@Override
	public PriceProduct update(PriceProduct priceProduct) {
		if (priceProduct.getIdPrice() == null || !priceProductDao.existsById(priceProduct.getIdPrice())) {
			throw new IllegalArgumentException("El PriceProduct no existe en la base de datos.");
		}
		return priceProductDao.save(priceProduct);
	}

	@Override
	public List<PriceProduct> findByCategory(ProductCategory category) {
		return priceProductDao.findByCategory(category);
	}
	
	@Override
	public List<PriceProduct> findPriceProductsWithoutProductId() {
	    return priceProductDao.findByProductIsNull();
	}
	
	@Override
	public List<PriceProduct> findByProductId(Product product) {
        return priceProductDao.findByProductOrderByPriceAsc(product.getIdProduct());
    }
	
	@Override
    public List<PriceProduct> findByProductAndUrl(Product product, String url) {
        return priceProductDao.findByProductAndUrl(product, url);
    }
	
	@Override
    public List<PriceProduct> findByProductAndUrlContaining(Product product, String baseUurl) {
		String url = "test";
		if(baseUurl.contains(WebScrapingConstants.COOLMOD)) {
    		url = WebScrapingConstants.COOLMOD;
    	}
    	if(baseUurl.contains(WebScrapingConstants.PCCOMPONENTES)) {
    		url = WebScrapingConstants.PCCOMPONENTES;
    	}
    	if(baseUurl.contains(WebScrapingConstants.AMAZON)) {
        	url = WebScrapingConstants.AMAZON;
        }
		return priceProductDao.findByProductAndUrlContaining(product, url);
    }
	
	@Override
	public PriceProduct createOrUpdatePriceProduct(String name, double price, String url, Date lastDate, ProductCategory category, String image) {
	    List<Product> productsWithSameName = productService.findByName(name);
	    Product product;
	    if (!productsWithSameName.isEmpty()) {
	        product = productsWithSameName.get(0);
	        List<PriceProduct> existingPriceProducts = findByProductAndUrlContaining(product, url);
	        if (!existingPriceProducts.isEmpty()) {
		        PriceProduct priceProductToUpdate = existingPriceProducts.get(0);
		        priceProductToUpdate.setPrice(price);
		        priceProductToUpdate.setLastDate(lastDate);
		        priceProductToUpdate.setUrl(url);
		        return update(priceProductToUpdate);
		    }
	        else {
		        PriceProduct newPriceProduct = new PriceProduct(name, price, url, lastDate, category, image);
		        newPriceProduct.setProduct(product);
		        return create(newPriceProduct);
		    }
	    } else {
	    	PriceProduct newPriceProduct = new PriceProduct(name, price, url, lastDate, category, image);
	        return create(newPriceProduct);
	    }
	}

	@Override
	public void deletePriceProducts(String baseUrl, ProductCategory category) {
		String url = "test";
		if(baseUrl.contains(WebScrapingConstants.COOLMOD)) {
    		url = WebScrapingConstants.COOLMOD;
    	}
    	if(baseUrl.contains(WebScrapingConstants.PCCOMPONENTES)) {
    		url = WebScrapingConstants.PCCOMPONENTES;
    	}
    	if(baseUrl.contains(WebScrapingConstants.AMAZON)) {
        	url = WebScrapingConstants.AMAZON;
        }
    	priceProductDao.deleteByUrlContainingAndCategory(url, category);
	}
}
