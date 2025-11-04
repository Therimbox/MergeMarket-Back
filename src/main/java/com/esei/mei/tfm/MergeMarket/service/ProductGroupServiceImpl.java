package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.dao.ProductGroupDao;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.entity.ProductGroup;

@Service
public class ProductGroupServiceImpl implements ProductGroupService {

	@Autowired
    private ProductGroupDao productGroupDao;
	
	@Autowired
    private ProductCategoryService productCategoryService;

    @Override
    public void initializeProductGroup() {
    	initializeProductGroupGpu(productCategoryService.findById(WebScrapingConstants.CATEGORIA_TARJETA_GRAFICA));
    }
    
    private void initializeProductGroupGpu(ProductCategory productCategory) {
    	String[] groupsGPU = {
    			"RTX 3090 Ti", "RTX 3080 Ti", "RTX 4070 Ti Super", "RTX 4070 Super", "RTX 4080 Super", "rx 580",
                "RTX 3070 Ti", "RTX 3060 Ti", "RTX 4070 Ti", "RTX 4060 Ti", "RTX 4090", "gtx 1660 ti", "gtx 1660 super",
                "RTX 3060", "RTX 4080", "RTX 3070", "RTX 4070", "RTX 4060", "RTX 3090", "RX 6900 XT", "rx 6500xt",
                "RX 6800 XT", "RX 6700 XT", "RX 6600 XT", "rx 6500 xt","RX 5700 XT", "RX 7900 XT", "RX 6750 XT",
                "RX 7800 XT", "RX 6950XT", "RX 7700 XT", "RX 6650 XT", "RX 6950 XT", "rx 6750xt", "RX 7900", "RX 6600", 
                "RX 7600 XT", "RTX 2060", "GT 1030", "RTX 3050", "GT 710", "GT 730", "rx 7600", "gtx 1650",
                "gtx 1050 ti", "rtx 3080", "rx 550", "rx 6800", "rx 6700", "gtx 1630", "rx 6400", "RTX 5090", "RTX 5080", 
                "RTX 5070 Ti", "RTX 5070", "RTX 5060", "RTX 5050", "RX 9070 XT", "RX 9070 GRE", "RX 9060 XT", "RX 8900 XT", 
                "RX 8800 XT", "RX 8700 XT","Arc B580", "Geforce 210", "gt 610", "arc a310", "arc a380", "radeon 550", 
                "arc a750", "radeon wx3200", "radeon rx 560", "gtx 1050", "gtx 1050ti", "quadro p620", "gtx 1660", 
                "nvidia t400", "radeon rx 5600 xt", "rtx a400", "gtx 1660ti", "arc b570", "rtx a2000", "arc b580", 
                "quadro p400", "quadro p1000", "rtx 4060", "arc a770", "radeon w7500", "rtx a1000", "arc pro b50", 
                "radeon rx 9070", "rtx 2000 ada", "rtx pro 2000", "radeon w5700", "rtx a4000", "rtx 4000 ada", "rtx pro 4000", 
                "rtx pro 4500", "rtx 4500 ada", "l4", "radeon w7900", "rtx pro 5000", "rtx 6000 ada", "rtx pro 6000", 
                "rtx pro 6000 max-q", "rtx pro 6000 workstation edition", "otro"
        };

        for (String group : groupsGPU) { 	
        	if (null == productGroupDao.findByName(group)){
        		ProductGroup productGroup = new ProductGroup(group, productCategory);
        		productGroupDao.save(productGroup);

                if (group.equalsIgnoreCase("otro")) {
                    System.out.println("GPU classified in 'otro' group: " + group);
                }
        	}
        }
    }
    
    @Override
    public List<ProductGroup> findAll() {
        return productGroupDao.findAll();
    }
}

