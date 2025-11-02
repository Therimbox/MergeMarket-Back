package com.esei.mei.tfm.MergeMarket.controller;

import com.esei.mei.tfm.MergeMarket.security.JwtTokenUtil;
import com.esei.mei.tfm.MergeMarket.entity.User;
import com.esei.mei.tfm.MergeMarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	 @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private UserService userService;

	    @Autowired
	    private JwtTokenUtil jwtTokenUtil;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @PostMapping("/register")
	    public ResponseEntity<?> registerUser(@RequestBody User user) {
	        // Encode the password before saving
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        User savedUser = userService.registerUser(user);
	        return new ResponseEntity<>(savedUser, HttpStatus.OK);
	    }

	    @PostMapping("/login")
	    public ResponseEntity<?> loginUser(@RequestBody User user) {
	        try {
	            Authentication authentication = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
	            );
	            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
	            String token = jwtTokenUtil.generateToken(userDetails);
	            return ResponseEntity.ok(token);
	        } catch (BadCredentialsException e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
	        }
	    }
}

