package com.esei.mei.tfm.MergeMarket.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.User;

public interface UserService {
    
	void initializeUsers();
	User registerUser(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    void addProductToTracking(Long userId, Long productId);
    List<Product> getProductsInTracking(Long userId);
    void removeProductFromTracking(Long userId, Long product);
	Boolean isTracking(Long userId, Long productId);
}
