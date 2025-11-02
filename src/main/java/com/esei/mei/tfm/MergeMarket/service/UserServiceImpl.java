package com.esei.mei.tfm.MergeMarket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.esei.mei.tfm.MergeMarket.dao.ProductDao;
import com.esei.mei.tfm.MergeMarket.dao.ProductTrackingDao;
import com.esei.mei.tfm.MergeMarket.dao.UserDao;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductTracking;
import com.esei.mei.tfm.MergeMarket.entity.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private ProductTrackingDao productTrackingDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void initializeUsers() {
        List<User> existingUsers = userDao.findAll();

        if(existingUsers.isEmpty()) {
        	User user = new User(1L, "admin", "mergemarket", "admin.mergemarket@uvigo.es", "admin");
        	user.setPassword(passwordEncoder.encode(user.getPassword()));
        	registerUser(user);
        }
    }

    @Override
    public User registerUser(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        return userDao.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
    
    @Override
    public void addProductToTracking(Long userId, Long productId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductTracking productTracking = new ProductTracking();
        productTracking.setUser(user);
        productTracking.setProduct(product);
        productTracking.setPrice(product.getPrice());
		productTracking.setAddedAt(LocalDateTime.now());
		productTrackingDao.save(productTracking);
        
        user.getProductsTracking().add(productTracking);
        userDao.save(user);
    }
    
    @Override
    public List<Product> getProductsInTracking(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<ProductTracking> productTrackings = user.getProductsTracking();
        return productTrackings.stream()
                .map(ProductTracking::getProduct)
                .collect(Collectors.toList());
    }
    
    @Override
    public void removeProductFromTracking(Long userId, Long productId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductTracking productTrackingToRemove = null;
        for (ProductTracking productTracking : user.getProductsTracking()) {
            if (productTracking.getProduct().getIdProduct().equals(productId)) {
                productTrackingToRemove = productTracking;
                break;
            }
        }

        if (productTrackingToRemove != null) {
            user.getProductsTracking().remove(productTrackingToRemove);
            userDao.save(user);
        } else {
            throw new RuntimeException("Product not found in user's tracking list");
        }
    }

	@Override
	public Boolean isTracking(Long userId, Long productId) {
		List<Product> products = getProductsInTracking(userId);
		Boolean isTracking = false;
		for(Product product : products) {
			if(product.getIdProduct().equals(productId)) {
				isTracking = true;
			}
		}
		return isTracking;
	}

}
