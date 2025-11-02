package com.esei.mei.tfm.MergeMarket.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PriceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrice;

    @ManyToOne
    private Product product;
    
    private String name;
    private double price;
    private String url;
    private Date lastDate;
    @ManyToOne
    private ProductCategory category;
    private String image;
	
    public PriceProduct() {
		super();
	}

	public PriceProduct(String name, double price, String url, Date lastDate, ProductCategory category, String image) {
		super();
		this.name = name;
		this.price = price;
		this.url = url;
		this.lastDate = lastDate;
		this.category = category;
		this.image = image;
	}

	public Long getIdPrice() {
		return idPrice;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public String getUrl() {
		return url;
	}

	public Date getLastDate() {
		return lastDate;
	}
	
	public ProductCategory getCategory() {
		return category;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public String getImage() {
		return image;
	}

	public void setIdPrice(Long idPrice) {
		this.idPrice = idPrice;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
