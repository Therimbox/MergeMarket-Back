package com.esei.mei.tfm.MergeMarket.service.scraping;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class SchemaScraperBase implements StoreScraper {

	@Override
	public abstract String getStoreName();

	@Override
	public abstract String buildPageURL(String baseUrl, int pageNumber);

	@Override
	public List<Product> extractPageProducts(Document document, List<String> names, String baseUrl, int pageNumber,
			List<String> urls, List<String> images, ProductCategory category) {
        
		List<Product> products = new ArrayList<>();
		
        Integer initialSize = names.size();
		Elements scripts = document.select("script");
        String itemList = null;

       	for (Element script : scripts) {
            if (script.attr("type").equals("application/ld+json") && script.data().contains("\"@type\":\"ItemList\"")) {
                itemList = script.data();
                break;
            }
        }
        
        if (itemList != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(itemList, JsonObject.class);
            JsonArray itemListElement = jsonObject.getAsJsonArray("itemListElement");
            for (JsonElement element : itemListElement) {
                JsonObject item = element.getAsJsonObject();
                if (item.get("name") == null) {
                    item = (JsonObject) item.get("item");
                }
                String name = item.get("name").getAsString();
                String url = item.get("url").getAsString();
                String image = item.get("image").getAsString();
                
                if(baseUrl.contains(WebScrapingConstants.COOLMOD)) {
                	if(!names.contains(name)) {
                    	names.add(name);
                    	urls.add(url);
                    	images.add(image);
                    }
                    if(names.size()-initialSize == 24) {
                    	break;
                    }
                }else {
                	names.add(name + " ");
                	urls.add(url);
                	images.add(image);
                }                
            }
        }
		for (int i = initialSize; i < names.size(); i++) {
			products.add(new Product(category, names.get(i), urls.get(i), images.get(i)));
		}
		for (Product product  : products) {
            Elements elements = document.select("[data-itemname=\"" + product.getName() + "\"]");
            if(elements.isEmpty()) {
            	elements = document.select("[data-product-name=\"" + product.getName() + "\"]");
            }
            if (!elements.isEmpty()) {
                Element element = elements.first();
                String price = "";
                if(element.attr("data-itemprice") != null && !element.attr("data-itemprice").equals("")) {
                	price = element.attr("data-itemprice");
                }else {
                	price = element.attr("data-product-price");
                }
                product.setPrice(Double.parseDouble(price));
            }
        }     
        
        return products;
	}
}
