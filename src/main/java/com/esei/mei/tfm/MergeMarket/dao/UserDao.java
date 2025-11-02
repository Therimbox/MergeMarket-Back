package com.esei.mei.tfm.MergeMarket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esei.mei.tfm.MergeMarket.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String login);
}

