package com.esei.mei.tfm.MergeMarket.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Product {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @ManyToOne
    private ProductCategory category;

    private String name;
    private Double price;
    private String web;
    private String image;
    private Date lastDate;
    
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<PriceHistory> priceHistories;
    
	public Product() {
		super();
	}
	public Product(ProductCategory category, String name, String web, String image) {
		super();
		this.category = category;
		this.name = name;
		this.web = web;
		this.image = image;
	}
	public Product(ProductCategory category, String name, Double price, String web, String image) {
		super();
		this.category = category;
		this.name = name;
		this.price = price;
		this.web = web;
		this.image = image;
		this.lastDate = new Date();
	}
	public Long getIdProduct() {
		return idProduct;
	}
	public ProductCategory getCategory() {
		return category;
	}
	public String getName() {
		return name;
	}
	public Double getPrice() {
		return price;
	}
	public String getWeb() {
		return web;
	}
	public String getImage() {
		return image;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public List<PriceHistory> getPriceHistories() {
        return priceHistories;
    }
	public void setIdProduct(Long idProduct) {
		this.idProduct = idProduct;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public void setPriceHistories(List<PriceHistory> priceHistories) {
        this.priceHistories = priceHistories;
    }

    public void updatePrice(Double newPrice) {
        if (this.price != null && !this.price.equals(newPrice)) {
            PriceHistory priceHistory = new PriceHistory(this, this.price, this.lastDate);
            this.priceHistories.add(priceHistory);
        }
        this.price = newPrice;
    }
}