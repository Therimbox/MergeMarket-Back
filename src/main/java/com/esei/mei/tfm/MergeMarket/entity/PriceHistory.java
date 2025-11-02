package com.esei.mei.tfm.MergeMarket.entity;

import javax.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Double price;
    
    private LocalDateTime timestamp;

    public PriceHistory() {
        super();
    }

    public PriceHistory(Product product, Double price, Date lastDate) {
        this.product = product;
        this.price = price;
        if(null != lastDate) {
        	Instant instant = lastDate.toInstant();
            this.timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }else {
        	this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
