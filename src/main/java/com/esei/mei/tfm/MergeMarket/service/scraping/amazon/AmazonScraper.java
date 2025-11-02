package com.esei.mei.tfm.MergeMarket.service.scraping.amazon;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.service.scraping.StoreScraper;

@Component
public class AmazonScraper implements StoreScraper {

	@Override
	public String getStoreName() {
		return WebScrapingConstants.AMAZON;
	}

	@Override
	public String buildPageURL(String baseUrl, int pageNumber) {
		return baseUrl + "?ie=UTF8&pg=" + pageNumber;
	}

	@Override
	public List<Product> extractPageProducts(Document document, List<String> names, String baseUrl, int pageNumber,
			List<String> urls, List<String> images, ProductCategory category) {

		List<Product> products = new ArrayList<>();
		
    	Elements divProducts = document.select("div#gridItemRoot");
    	for (Element div : divProducts) {
            Element imagen = div.select("img").first();
            String srcImagen = imagen.attr("src");
            String nombre = imagen.attr("alt");
            
            Element precioElement = new Element(nombre);
            if(div.select("span.a-color-price").isEmpty()) {
            	precioElement = div.select("span.p13n-sc-price").first();
            }else {
            	precioElement = div.select("span.a-color-price").first();
            }
            double precio = 0.0;
            if(null != precioElement) {
            	String precioText = precioElement.text();
            	precioText = precioText.replace("€", "").replace(",", ".");
            	precio = Double.parseDouble(precioText);
            }

            Element enlaceElement = div.select("a").first();
            String enlace = "https://www.amazon.es" + enlaceElement.attr("href");

            Product product = new Product(category, nombre, precio, enlace, srcImagen);
            products.add(product);
    	}
    	return products;
	}


}
