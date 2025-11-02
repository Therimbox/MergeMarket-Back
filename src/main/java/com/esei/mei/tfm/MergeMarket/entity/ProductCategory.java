package com.esei.mei.tfm.MergeMarket.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProductCategory {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String image;
    
    private boolean hasGroups;
    
    public ProductCategory() {
		super();
	}

	public ProductCategory(String name, String image, boolean hasGroups) {
		super();
		this.name = name;
		this.image = image;
		this.hasGroups = hasGroups;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isHasGroups() {
		return hasGroups;
	}

	public void setHasGroups(boolean hasGroups) {
		this.hasGroups = hasGroups;
	}
}
