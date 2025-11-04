package com.esei.mei.tfm.MergeMarket.service.scraping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences; // Correct import for Selenium
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@EnableScheduling
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
    	System.setProperty("webdriver.chrome.driver", "/app/drivers/chromedriver"); // Correct path for Linux
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
                System.out.println("Scraping URL: " + url);
                try {
                    String html;
                    List<Product> pageProducts = new ArrayList<>();
                    html = this.getHtmlSelenium(url);
                    System.out.println("HTML content length: " + (html != null ? html.length() : 0));
                    Document document = Jsoup.parse(html);

                    pageProducts = scraper.extractPageProducts(document, names, baseUrl, pageNumber, urls, images, category);
                    System.out.println("Products found on page " + pageNumber + ": " + pageProducts.size());
                    if (pageProducts.isEmpty()) {
                        foundProducts = false;
                    } else {
                        products.addAll(pageProducts);
                        pageNumber++;
                    }
                } catch (IOException e) {
                    System.err.println("Error scraping URL: " + url);
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("No scraper found for base URL: " + baseUrl);
        }

        System.out.println("Total products scraped: " + products.size());
        return products;
    }

    
	private String getHtmlSelenium(String url) throws IOException {
        System.setProperty("webdriver.chrome.driver", "/app/drivers/chromedriver"); // Correct path for Linux
        String itemList = null;
        String html = null;
        Integer test = 0;
        Integer timeWait = 1000; // Increased wait time to simulate human behavior
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.addArguments("--headless"); // Ejecutar en modo sin cabeza
        options.addArguments("--no-sandbox"); // Deshabilitar sandbox
        options.addArguments("--disable-dev-shm-usage"); // Evitar problemas de memoria compartida
        options.addArguments("--disable-gpu"); // Deshabilitar GPU
        options.addArguments("--window-size=1920,1080"); // Tamaño de ventana predeterminado
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"); // Realistic User-Agent
        WebDriver driver = new ChromeDriver(options);

        driver.get(url);
        System.out.println("Navigated to URL: " + url);

        if (url.contains(WebScrapingConstants.AMAZON)) {
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

        while (itemList == null && test < 3) {
            try {
                Thread.sleep(timeWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            html = driver.getPageSource();

            // Check for captcha and refresh the page if detected
            if (html.contains("Verifying you are human")) {
                System.out.println("Captcha detected. Refreshing the page...");
                driver.navigate().refresh();
                try {
                    Thread.sleep(5000); // Wait for the page to reload
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // Save the HTML content to a file for debugging
            try {
                String sanitizedUrl = url.replaceAll("[\\/:*?\"<>|]", "_");
                Path outputPath = Paths.get("/home/extracciones", sanitizedUrl + ".html");
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, html.getBytes(StandardCharsets.UTF_8));
                System.out.println("HTML content saved to: " + outputPath);
            } catch (IOException e) {
                System.err.println("Failed to save HTML content: " + e.getMessage());
            }

            itemList = html;
            test++;
        }
        driver.quit();
        return html;
    }

    @Scheduled(cron = "0 10 2 * * ?") // Ejecutar todos los días a las 2:10 AM
    public void scrapeCategory1() {
        System.out.println("Iniciando scraping programado para la categoría Procesadores...");
        scrapeProducts("https://www.pccomponentes.com/procesadores", ProductCategory.CATEGORY_1);
        scrapeProducts("https://www.coolmod.com/componentes-pc-procesadores/", ProductCategory.CATEGORY_1);
        scrapeProducts("https://www.amazon.es/gp/bestsellers/computers/937925031/ref=zg_bs_pg_2_computers", ProductCategory.CATEGORY_1);
        System.out.println("Scraping programado para la categoría Procesadores completado.");
    }

    @Scheduled(cron = "0 20 3 * * ?") // Ejecutar todos los días a las 3:20 AM
    public void scrapeCategory2() {
        System.out.println("Iniciando scraping programado para la categoría Tarjetas Gráficas...");
        scrapeProducts("https://www.pccomponentes.com/tarjetas-graficas", ProductCategory.CATEGORY_2);
        scrapeProducts("https://www.coolmod.com/tarjetas-graficas/", ProductCategory.CATEGORY_2);
        System.out.println("Scraping programado para la categoría Tarjetas Gráficas completado.");
    }

    @Scheduled(cron = "0 30 4 * * ?") // Ejecutar todos los días a las 4:30 AM
    public void scrapeCategory3() {
        System.out.println("Iniciando scraping programado para la categoría Placas Base...");
        scrapeProducts("https://www.pccomponentes.com/placas-base", ProductCategory.CATEGORY_3);
        scrapeProducts("https://www.coolmod.com/componentes-pc-placas-base/", ProductCategory.CATEGORY_3);
        System.out.println("Scraping programado para la categoría Placas Base completado.");
    }

    @Scheduled(cron = "0 40 5 * * ?") // Ejecutar todos los días a las 5:40 AM
    public void scrapeCategory4() {
        System.out.println("Iniciando scraping programado para la categoría RAM...");
        scrapeProducts("https://www.pccomponentes.com/memorias-ram", ProductCategory.CATEGORY_4);
        System.out.println("Scraping programado para la categoría RAM completado.");
    }
}
