package com.esei.mei.tfm.MergeMarket.service.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;


@Service
public class StoreScraperManager {

	@Autowired
	private List<StoreScraper> availableStoreScrapers;
	private Map<String, StoreScraper> registeredStoreScrapers = new HashMap<>();
	
	@PostConstruct
	private void initilize() {
		registerScrappers();
		initWebDriver();
	}
	
	
	private void initWebDriver() {
    	System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
	}
	
	private void registerScrappers() {
		for (StoreScraper scraper : availableStoreScrapers) {
			System.out.println("Carga "+scraper.getStoreName());
			registeredStoreScrapers.put(scraper.getStoreName(), scraper);
		}
	}

	private StoreScraper findScraperForURL(String baseURL) {
		for (Map.Entry<String, StoreScraper> entry: this.registeredStoreScrapers.entrySet()) {
			if (baseURL.contains(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
    public List<Product> scrapeProducts(String baseUrl, ProductCategory category) {
        List<Product> products = new ArrayList<>();

    	StoreScraper scraper = this.findScraperForURL(baseUrl);
    	if (scraper != null) {
            List<String> names = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            List<String> images = new ArrayList<>();

            int pageNumber = 1;
            boolean foundProducts = true;

            while (foundProducts) {
            	String url = scraper.buildPageURL(baseUrl, pageNumber);
                try {
                    String html;
                    List<Product> pageProducts= new ArrayList<>();
                    html = this.getHtmlSelenium(url);
                    Document document = Jsoup.parse(html);

                    pageProducts = scraper.extractPageProducts(document, names, baseUrl, pageNumber, urls, images, category);
                    if (pageProducts.isEmpty()) {
                        foundProducts = false;
                    } else {
                        products.addAll(pageProducts);
                        pageNumber++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }    		
    	}
    	
    	return products;   	
    }

    
	private  String getHtmlSelenium(String url) throws IOException {
        String itemList = null;
        String html = null;
        Integer test = 0;
        Integer timeWait = 500;
        WebDriver driver = new ChromeDriver();		

        driver.get(url);
        if(url.contains(WebScrapingConstants.AMAZON)) {
        	JavascriptExecutor js = (JavascriptExecutor) driver;
        	js.executeScript("var scrollStep = window.innerHeight / 20;" +
                    "function scrollToBottom() {" +
                    "    if (window.scrollY < document.body.scrollHeight - window.innerHeight) {" +
                    "        window.scrollBy(0, scrollStep);" +
                    "        setTimeout(scrollToBottom, 30);" +
                    "    }" +
                    "} scrollToBottom();");
        	timeWait = 4000;
        }
        while(itemList == null && test < 3) {
        	try {
                Thread.sleep(timeWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        	html = driver.getPageSource();
            Document document = Jsoup.parse(html);
            Elements scripts = document.select("script");
            for (Element script : scripts) {
                if (script.attr("type").equals("application/ld+json") && script.data().contains("\"@type\":\"ItemList\"")) {
                    itemList = script.data();
                    break;
                }
            }
            if(itemList == null) {
            	test++;
            }
        }
        driver.quit();    
        return html;        
    }
}
