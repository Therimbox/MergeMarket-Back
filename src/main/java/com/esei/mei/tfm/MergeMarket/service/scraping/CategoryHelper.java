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
		name = name.replaceFirst("Processor", "");
		name = name.replaceFirst("BOX", "");
		name = name.replaceAll("(?i)(\\d+\\s*N[úu]cleos|N[úu]cleos\\s*\\d+)", "");

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
		// Eliminar prefijo "Placa Base"
		name = name.replaceAll("(?i)^Placa\\s+Base\\s+", "");
		
		// Correcciones ortográficas comunes
		name = name.replaceAll("(?i)Livemixer", "Live Mixer");
		name = name.replaceAll("(?i)Ligthning", "Lightning");
		
		// Eliminar especificaciones técnicas de chipset/socket en medio del nombre
		name = name.replaceAll("(?i)\\s+(Intel|Amd)\\s+(A520|A620|A320|B450|B550|B650|B650e|B650m|B660|B760|B840|B850|B860|X570|X670|X670e|X870|X870e|H410|H510|H610|H770|H810|Z490|Z590|Z690|Z790|Z890|Q570|Q670|Q870|W680|W790|W880|C256|C262|C266|C741|Trx50|Wrx80|Wrx90|X299|Epyc|Rome|Turin|Sienad8ud3)\\b", "");
		
		// Eliminar sockets y especificaciones
		name = name.replaceAll("(?i)\\s+(Lga\\s?\\d+|Am4|Am5|Str5|Sp3|Sp5|Sp6|Swrx8|Socket\\s+\\w+)", "");
		
		// Eliminar formatos de placa
		name = name.replaceAll("(?i)\\s+(Atx|Micro\\s?Atx|Mini\\s?Itx|E-Atx|Eeb|Ceb|Ssi\\s?Ceb|Micro-Atx|Mini-Itx)", "");
		
		// Eliminar tipos de RAM
		name = name.replaceAll("(?i)\\s+(Ddr3|Ddr4|Ddr5|D5)", "");
		
		// Eliminar conectividad
		name = name.replaceAll("(?i)\\s+(Wifi\\s?\\d?|Wi-Fi\\s?\\d?|Ax|Ac|Bluetooth\\s?[\\d.]+)", "");
		
		// Eliminar características de almacenamiento
		name = name.replaceAll("(?i)\\s+(/M\\.2\\+?|M\\.2\\+?|Dual\\s+M\\.2)", "");
		
		// Eliminar versiones y revisiones (al final del nombre)
		name = name.replaceAll("(?i)\\s+(V2|V3|R2\\.0|Ii|Iii|2\\.0|3\\.0|\\(.*?\\))\\s*$", "");
		
		// Eliminar características adicionales
		name = name.replaceAll("(?i)\\s+(Argb|Rgb|Ice|White|Blanca|Black|Se|Btf|Plus)", "");
		
		// Eliminar conectores y especificaciones técnicas
		name = name.replaceAll("(?i)\\s+(\\+|Pcie\\s?[\\d.]+|Usb\\s?[\\d.]+|Gen\\s?\\d+|Thunderbolt\\s?\\d+|Lan|Gbe|\\d+gbe|\\d+\\.?\\d*g\\s?Lan)", "");
		
		// Eliminar capacidades de RAM y otras especificaciones numéricas largas
		name = name.replaceAll("(?i)\\s+(\\d+gb|\\d+tb|\\d+\\s?Dimm|Ecc|Raid|Ipmi|Ast\\d+)", "");
		
		// Eliminar sufijos técnicos
		name = name.replaceAll("(?i)\\s+(Sata\\s?Iii?|Nvme|Soporta|Compatibilidad|Avanzada|Robusta|Server|Gaming|Creatividad|Audio|Uefi|Overclocking)", "");
		
		// Eliminar caracteres especiales al final
		name = name.replaceAll("[-/]+$", "");
		
		// Normalizar espacios múltiples
		name = name.replaceAll("\\s{2,}", " ");
		
		// Convertir a lowercase primero para trabajar con el texto
		String productName = name.toLowerCase().trim();
		
		// Eliminar espacios y guiones redundantes
		productName = productName.replaceAll("\\s*-\\s*", "-");
		productName = productName.replaceAll("\\s*/\\s*", "/");
		
		// Convertir a CamelCase (primera letra de cada palabra en mayúscula)
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
