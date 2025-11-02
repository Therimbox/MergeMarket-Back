package com.esei.mei.tfm.MergeMarket.service.scraping.pccomponentes;

import org.springframework.stereotype.Component;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.service.scraping.SchemaScraperBase;

@Component
public class PCComponentesScraper extends SchemaScraperBase {

	@Override
	public String getStoreName() {
		return WebScrapingConstants.PCCOMPONENTES;
	}

	@Override
	public String buildPageURL(String baseUrl, int pageNumber) {
		return baseUrl + "?page=" + pageNumber;
	}

}
