package com.esei.mei.tfm.MergeMarket;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.esei.mei.tfm.MergeMarket.service.ProductCategoryService;
import com.esei.mei.tfm.MergeMarket.service.ProductGroupService;
import com.esei.mei.tfm.MergeMarket.service.UserService;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
public class MergeMarketApplication {


	@Autowired
	private ProductGroupService productGroupService;
	
	@Autowired
	private ProductCategoryService productCategoryService;
	
	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(MergeMarketApplication.class, args);
	}

	@PostConstruct
	public void init() {
		userService.initializeUsers();
		productCategoryService.initializeCategories();
		productGroupService.initializeProductGroup();		
	}

	
}
