package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.dao.ProductCategoryDao;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductCategoryDao productCategoryDao;

	@Override
	public void initializeCategories() {
		List<ProductCategory> existingCategories = productCategoryDao.findAll();

		createCategoryIfNotExists(WebScrapingConstants.CATEGORIA_PROCESADOR,
				WebScrapingConstants.CATEGORIA_PROCESADOR_NAME, WebScrapingConstants.CATEGORIA_PROCESADOR_IMAGE,
				WebScrapingConstants.CATEGORIA_PROCESADOR_GROUPS, existingCategories);

		createCategoryIfNotExists(WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA,
				WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA_NAME,
				WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA_IMAGE,
				WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA_GROUPS, existingCategories);

		createCategoryIfNotExists(WebScrapingConstants.CATEGORIA_PLACA_BASE,
				WebScrapingConstants.CATEGORIA_PLACA_BASE_NAME, WebScrapingConstants.CATEGORIA_PLACA_BASE_IMAGE,
				WebScrapingConstants.CATEGORIA_PLACA_BASE_GROUPS, existingCategories);
		createCategoryIfNotExists(WebScrapingConstants.CATEGORIA_RAM, WebScrapingConstants.CATEGORIA_RAM_NAME,
				WebScrapingConstants.CATEGORIA_RAM_IMAGE, WebScrapingConstants.CATEGORIA_RAM_GROUPS,
				existingCategories);

	}

	private void createCategoryIfNotExists(Long id, String name, String image, Boolean hasGroups,
			List<ProductCategory> existingCategories) {
		boolean exists = existingCategories.stream().anyMatch(category -> category.getName().equals(name));
		if (!exists) {
			ProductCategory newCategory = new ProductCategory(name, image, hasGroups);
			newCategory.setId(id);
			productCategoryDao.save(newCategory);
		}
	}

	@Override
	public ProductCategory create(ProductCategory productCategory) {
		return productCategoryDao.save(productCategory);
	}

	@Override
	public ProductCategory findById(Long id) {
		Optional<ProductCategory> productCategory = productCategoryDao.findById(id);
		ProductCategory result = null;
		if (productCategory.isPresent()) {
			result = productCategory.get();
		}
		return result;
	}

	@Override
	public List<ProductCategory> findAll() {
		return productCategoryDao.findAll();
	}
}
