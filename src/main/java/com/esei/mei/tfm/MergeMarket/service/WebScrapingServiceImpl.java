package com.esei.mei.tfm.MergeMarket.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.entity.PriceHistory;
import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.entity.ProductGroup;
import com.esei.mei.tfm.MergeMarket.service.scraping.CategoryHelper;
import com.esei.mei.tfm.MergeMarket.service.scraping.StoreScraperManager;

@Service
public class WebScrapingServiceImpl implements WebScrapingService {

	@Autowired
	private ProductService productService;
	@Autowired
	private PriceProductService priceProductService;
	@Autowired
	private ProductGroupService productGroupService;
	@Autowired
	private PriceHistoryService priceHistoryService;

	@Autowired
	private StoreScraperManager scraperManager;

	@Autowired
	private CategoryHelper categoryHelper;

	@Override
	public List<Product> scrapeAndSaveProducts(String baseUrl, ProductCategory category) {
		List<Product> products = scraperManager.scrapeProducts(baseUrl, category);
		priceProductService.deletePriceProducts(baseUrl, category);
		for (Product product : products) {
			savePriceForProduct(product, baseUrl, category);
		}
		insertProducts(category);
		assignProductsToPriceProducts();
		comparePrices(category);
		updateActivoForCategory(category);
		return products;
	}

	private void updateActivoForCategory(ProductCategory category) {
		List<Product> products = productService.findByCategory(category);
		for (Product product : products) {
			List<PriceProduct> priceProducts = priceProductService.findByProductId(product);
			if (priceProducts != null && !priceProducts.isEmpty()) {
				product.setActivo(1);
			} else {
				product.setActivo(0);
			}
			productService.update(product);
		}
	}

	private void savePriceForProduct(Product product, String baseUrl, ProductCategory category) {
		double price = product.getPrice();
		String productName = categoryHelper.normaliceProductName(product.getName(), product.getWeb(), category);

		if (categoryHelper.validName(productName, category) && product.getPrice() != null && product.getPrice() > 30) {
			priceProductService.create(
					new PriceProduct(productName, price, product.getWeb(), new Date(), category, product.getImage()));
		}
	}

	@Override
	public void insertProducts(ProductCategory category) {
		List<PriceProduct> priceProductsList = priceProductService.findByCategory(category);
		List<Product> productsList = productService.findByCategory(category);

		if (category.isHasGroups()) {
			handleGroupProducts(category, priceProductsList, productsList);
		} else {
			handleNonGroupProducts(category, priceProductsList, productsList);
		}
	}

	private void handleGroupProducts(ProductCategory category, List<PriceProduct> priceProductsList,
			List<Product> productsList) {
		insertProductGroups(category, productsList);
		for (Product product : productsList) {
			List<PriceProduct> priceProductAux = new ArrayList<>();
			for (PriceProduct priceProduct : priceProductsList) {
				String productName = categoryHelper.normaliceProductName(priceProduct.getName(), priceProduct.getUrl(),
						category);
				if (productName.contains(product.getName().toLowerCase())) {
					priceProductAux.add(priceProduct);
					updateProductIfNecessary(product, priceProduct);
					priceProduct.setProduct(product);
					priceProductService.update(priceProduct);
					productService.update(product);
				}
			}
			priceProductsList.removeAll(priceProductAux);
		}
		if (productsList.isEmpty()) {
			productsList = productService.findByCategory(category);
		}
		for (PriceProduct priceProduct : priceProductsList) {
			System.out.println(priceProduct.getName());
			priceProduct.setProduct(productsList.get(productsList.size() - 1));
			priceProductService.update(priceProduct);
		}
	}

	private void insertProductGroups(ProductCategory category, List<Product> productsList) {
		Set<String> existingProductNames = getExistingProductNames(productsList);
		List<ProductGroup> productGroups = productGroupService.findAll();
		for (ProductGroup productGroup : productGroups) {
			String productName = productGroup.getName();
			if (!existingProductNames.contains(productName)) {
				Product product = new Product(category, productName, 0.0, "", "");
				productService.create(product);
				existingProductNames.add(productName);
			}
		}
	}

	private void handleNonGroupProducts(ProductCategory category, List<PriceProduct> priceProductsList,
			List<Product> productsList) {
		Set<String> existingProductNames = getExistingProductNames(productsList);
		for (PriceProduct priceProduct : priceProductsList) {
			String productName = categoryHelper.normaliceProductName(priceProduct.getName(), priceProduct.getUrl(),
					category);
			if (!existingProductNames.contains(productName)) {
				Product product = new Product(category, productName, priceProduct.getPrice(), priceProduct.getUrl(),
						priceProduct.getImage());
				productService.create(product);

				priceProduct.setProduct(product);
				priceProductService.update(priceProduct);

				existingProductNames.add(productName);
			} else {
				Product product = productService.findByName(productName).get(0);
				updateProductIfNecessary(product, priceProduct);
			}
		}
	}

	private Set<String> getExistingProductNames(List<Product> productsList) {
		Set<String> existingProductNames = new HashSet<>();
		for (Product product : productsList) {
			existingProductNames.add(product.getName());
		}
		return existingProductNames;
	}

	private void updateProductIfNecessary(Product product, PriceProduct priceProduct) {
		LocalDate today = LocalDate.now();
		List<PriceHistory> histories = priceHistoryService.findByProductIdProduct(product.getIdProduct());
		PriceHistory todayHistory = null;
		for (PriceHistory history : histories) {
			if (history.getTimestamp().toLocalDate().equals(today)) {
				todayHistory = history;
				break;
			}
		}

		if (todayHistory == null) {
			PriceHistory priceHistory = new PriceHistory(product, priceProduct.getPrice(), priceProduct.getLastDate());
			priceHistoryService.create(priceHistory);
		} else if (priceProduct.getPrice() < todayHistory.getPrice()) {
			todayHistory.setPrice(priceProduct.getPrice());
			todayHistory.setTimestamp(LocalDateTime.now());
			priceHistoryService.update(todayHistory);
		}

		if (product.getPrice() == 0.0 || product.getPrice() > priceProduct.getPrice()) {
			product.setImage(priceProduct.getImage());
			product.setPrice(priceProduct.getPrice());
			product.setWeb(priceProduct.getUrl());
			product.setLastDate(priceProduct.getLastDate());
			productService.update(product);
		}
	}

	@Override
	public void assignProductsToPriceProducts() {
		List<PriceProduct> priceProductsWithoutProductId = priceProductService.findPriceProductsWithoutProductId();
		for (PriceProduct priceProduct : priceProductsWithoutProductId) {
			String productName = categoryHelper.normaliceProductName(priceProduct.getName(), priceProduct.getUrl(),
					priceProduct.getCategory());
			List<Product> coincidences = productService.findProduct(productName, priceProduct.getCategory());
			if (coincidences.size() == 1) {
				priceProduct.setProduct(coincidences.get(0));
				priceProductService.update(priceProduct);
			}
		}
	}

	private void comparePrices(ProductCategory category) {
		List<Product> products = productService.findByCategory(category);
		for (Product product : products) {
			List<PriceProduct> priceProducts = priceProductService.findByProductId(product);
			boolean priceExists = priceProducts.stream()
					.anyMatch(priceProduct -> priceProduct.getPrice() == product.getPrice());
			if (!priceExists && !priceProducts.isEmpty()) {
				PriceProduct cheapestPriceProduct = priceProducts.stream()
						.min(Comparator.comparing(PriceProduct::getPrice))
						.orElse(null);
				if (cheapestPriceProduct != null) {
					product.setPrice(cheapestPriceProduct.getPrice());
					product.setWeb(cheapestPriceProduct.getUrl());
					productService.update(product);
				}
			}
		}
	}

}
