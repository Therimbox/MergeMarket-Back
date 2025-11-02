package com.esei.mei.tfm.MergeMarket.service.scraping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

@Component
public class CategoryHelper {

	public boolean validName(String productName, ProductCategory category) {
		boolean valid = true;
		if (category.getId().equals(WebScrapingConstants.CATEGORIA_PROCESADOR)) {
			if (productName.contains("PC") || productName.contains("Mini") || productName.contains("Arduino")) {
				valid = false;
			}
		}
		return valid;
	}

	public String normaliceProductName(String name, String web, ProductCategory category) {
		String productName = "";
		if (category.getId().equals(WebScrapingConstants.CATEGORIA_PROCESADOR)) {
			productName = normalizeCPUName(name, web);
		}
		if (category.getId().equals(WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA)) {
			productName = normalizeGPUName(name);
		}
		if (category.getId().equals(WebScrapingConstants.CATEGORIA_PLACA_BASE)) {
			productName = normalizePlacaBase(name);
		}
		if (category.getId().equals(WebScrapingConstants.CATEGORIA_RAM)) {
			productName = normalizeRam(name);
		}
		return productName;

	}

	private String normalizeRam(String name) {
	    name = name.toLowerCase();

	    String[] stopWords = {"memoria", "black", "white", "ram"};
	    for (String word : stopWords) {
	        name = name.replaceAll("\\b" + word + "\\b", "");
	    }

	    name = name.replaceAll("[^a-z0-9 ]", " ");

	    Map<String, String> synonyms = new HashMap<>();
	    synonyms.put("gb", "gb");
	    synonyms.put("gigabyte", "gb");
	    synonyms.put("mhz", "mhz");
	    synonyms.put("ddr4", "ddr4");
	    synonyms.put("pc4-25600", "");
	    synonyms.put("cl16", "cl16");
	    for (Map.Entry<String, String> entry : synonyms.entrySet()) {
	        name = name.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
	    }

	    name = name.trim().replaceAll(" +", " ");

	    Set<String> wordsSet = new TreeSet<>(Arrays.asList(name.split(" ")));
	    return String.join(" ", wordsSet);
	}


	private String normalizeCPUName(String name, String web) {
		String toret = "";

		name = name.replaceFirst("®", "");
		name = name.replaceFirst("™", "");
		name = name.replaceFirst("Procesador", "");
		name = name.replaceFirst("Box", "");
		name = name.replaceFirst("con*", "");
		name = name.replaceAll("\\s[^\\s]*Ghz.*", "");
		name = name.trim();

		if (name.contains(",")) {
			name = name.substring(0, name.indexOf(','));
		}
		if (name.contains("(")) {
			name = name.substring(0, name.indexOf('('));
		}
		if (name.contains("tecnol")) {
			name = name.substring(0, name.indexOf("tecnol"));
		}
		if (!name.contains("Intel")) {
			name = name.replaceAll("-", "");
		}

		String[] partes = name.split("\\s\\d+\\.?\\d*\\s?GHz(?:\\s.*|$)");
		String productNameAux = partes[0].trim();
		String productNameAux2 = productNameAux.replaceAll("\\s[^\\s]*Ghz.*", "");
		String productNameAux3 = productNameAux2.replaceAll("GHz.*", "").replaceAll("GHZ*", "");
		String productNameAux4 = productNameAux3.replaceAll("\\d+\\.\\d+", "");
		name = productNameAux4.replaceAll("\\(.*", "").replaceAll("/.*", "").replaceAll("\\s\\d$", "")
				.replaceAll("\\s+$", "");

		if (name.equals("Intel Core i9-13900F 2")) {
			name = "Intel Core i9-13900F";
		}
		if (name.contains("AMD Ryzen 7 5800X3D")) {
			name = "AMD Ryzen 7 5800X3D";
		}

		while (name.endsWith(" ")) {
			name = name.substring(0, name.length() - 1);
		}

		toret = name;
		return toret;
	}

	private String normalizeGPUName(String name) {
		name = name.replaceAll("(DLSS3?|\\d+-[Cc]lick\\s?[Oo][Cc])$", "");
		name = name.replaceAll("GDDR.*", "");
		name = name.replaceAll("(\\d+)\\s*\\s?GB", "$1GB");
		name = name.replaceAll("(?<=\\D)(\\d)", " $1");
		name = name.replace("edition", "").trim();
		name = name.trim().toLowerCase().replaceAll("\\s+", " ");

		return name;
	}

	private String normalizePlacaBase(String name) {
		name = name.replaceAll("(?i)Socket.*", "");
		name = name.replaceAll("D4", "");
		name = name.replaceAll("Ddr4", "");
		name = name.replaceAll("Livemixer", "Live Mixer");
		name = name.replaceAll("Ligthning", "Lightning");
		name = name.replaceAll("(?i)Wifi.*", "");
		name = name.replaceAll("(?i)Ddr4.*", "");
		name = name.replaceAll("(?i)Csm.*", "");
		name = name.replaceAll("-$", "");
		name = name.replaceAll("/$", "");
		name = name.replaceAll("\\s{2,}", " ");
		String productName = name.toLowerCase();

		Pattern pattern = Pattern.compile("\\b(\\w)");
		Matcher matcher = pattern.matcher(productName);

		StringBuffer camelCase = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(camelCase, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(camelCase);

		return camelCase.toString().trim();
	}

}
