package com.esei.mei.tfm.MergeMarket.service.scraping;

import java.util.List;

import org.jsoup.nodes.Document;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface StoreScraper {
    public String getStoreName();
    public String buildPageURL(String baseUrl, int pageNumber);
    public List<Product> extractPageProducts(Document document, List<String> names, String baseUrl, int pageNumber, List<String> urls, List<String> images, ProductCategory category);

}
