package com.esei.mei.tfm.MergeMarket.controller;

import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.User;
import com.esei.mei.tfm.MergeMarket.security.JwtTokenUtil;
import com.esei.mei.tfm.MergeMarket.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<User> getUserByToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String username = jwtTokenUtil.extractUsername(jwtToken);
            User user = userService.getUserByUsername(username);
            
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping("/{userId}/addTrack/{productId}")
    public ResponseEntity<?> addProductToTracking(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            userService.addProductToTracking(userId, productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/track")
    public ResponseEntity<List<Product>> getProductsInTracking(@PathVariable Long userId) {
        List<Product> products = userService.getProductsInTracking(userId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @DeleteMapping("/{userId}/deleteTrack/{productId}")
    public ResponseEntity<?> removeProductFromTracking(@PathVariable Long userId, @PathVariable Long productId) {
        try {
        	
            userService.removeProductFromTracking(userId, productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{userId}/isTracking/{productId}")
    public ResponseEntity<Boolean> isTracking(@PathVariable Long userId, @PathVariable Long productId) {
    	Boolean isTracking = userService.isTracking(userId, productId);
        return new ResponseEntity<>(isTracking, HttpStatus.OK);
    }

}
