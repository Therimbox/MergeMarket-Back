package com.esei.mei.tfm.MergeMarket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.dao.ProductDao;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductDao productDao;
	
	@Override
	public Product create(Product product){
        return productDao.save(product);
    }
	
	@Override
	public Product update(Product product) {
        if (product.getIdProduct() == null || !productDao.existsById(product.getIdProduct())) {
            throw new IllegalArgumentException("El Product no existe en la base de datos.");
        }
        return productDao.save(product);
    }
	
	@Override
    public List<Product> findByName(String name) {
        return productDao.findByName(name);
    }
	
	@Override
	public List<Product> findByCategory(ProductCategory category) {
		return productDao.findByCategory(category);
	}
	
	@Override
    public Optional<Product> findById(Long id) {
        return productDao.findById(id);
    }
		
	@Override
    public List<Product> findByKeyword(String keyword) {
        return productDao.findByKeyword(keyword);
    }
	
	@Override
    public List<Product> findProduct(String name, ProductCategory category) {
		List<Product> toret = new ArrayList<>();
		if(category.getId().equals(WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA)) {
			toret = findByKeyword(name);
		}else {
			toret = findByName(name);
		}
		return toret;
    }

	@Override
	public Product findMatchingProduct(String productName, ProductCategory category) {
		return productDao.findByCategory(category).stream()
			.filter(product -> product.getName().equalsIgnoreCase(productName))
			.findFirst()
			.orElse(null);
	}

	@Override
    public Product findByNameAndCategory(String name, ProductCategory category) {
        return productDao.findByCategory(category).stream()
            .filter(product -> product.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

	@Override
    public ProductCategory findCategoryById(Long id) {
        return productDao.findById(id)
            .map(Product::getCategory)
            .orElseThrow(() -> new IllegalArgumentException("Category not found for the given ID"));
    }

	@Override
	public List<Product> findFilteredSortedByCategory(ProductCategory category) {
		if (category.getId() != null && category.getId() == WebScrapingConstants.CATEGORIA_RAM) {
			return productDao.findByCategory(category).stream()
				.filter(p -> p.getPrice() != null && p.getPrice() > 0)
				.sorted(java.util.Comparator.comparing(Product::getPrice))
				.collect(java.util.stream.Collectors.toList());
		} else {
			return productDao.findByCategory(category).stream()
				.filter(p -> p.getPrice() != null && p.getPrice() > 0 && p.getActivo() == 1)
				.sorted(java.util.Comparator.comparing(Product::getPrice))
				.collect(java.util.stream.Collectors.toList());
		}
	}
}
