package com.esei.mei.tfm.MergeMarket.service.scraping.coolmod;

import org.springframework.stereotype.Component;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.service.scraping.SchemaScraperBase;

@Component
public class CoolmodScraper extends SchemaScraperBase {

	@Override
	public String getStoreName() {
		return WebScrapingConstants.COOLMOD;
	}

	@Override
	public String buildPageURL(String baseUrl, int pageNumber) {
		return baseUrl + "?pagina=" + pageNumber;
	}

}
