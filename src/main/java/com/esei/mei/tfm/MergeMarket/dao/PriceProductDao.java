package com.esei.mei.tfm.MergeMarket.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.esei.mei.tfm.MergeMarket.entity.PriceProduct;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;

public interface PriceProductDao extends JpaRepository<PriceProduct, Long> {

    List<PriceProduct> findByCategory(ProductCategory category);

    List<PriceProduct> findByProductIsNull();

    @Query(value = "SELECT * FROM priceproduct pp WHERE pp.\"product_idProduct\" = :productId ORDER BY pp.price ASC", nativeQuery = true)
    List<PriceProduct> findByProductOrderByPriceAsc(@Param("productId") Long productId);

    @Query("SELECT pp FROM PriceProduct pp WHERE pp.product = :product AND pp.url = :url")
    List<PriceProduct> findByProductAndUrl(@Param("product") Product product, @Param("url") String url);

    @Query("SELECT pp FROM PriceProduct pp WHERE pp.product = :product AND pp.url LIKE %:url%")
    List<PriceProduct> findByProductAndUrlContaining(@Param("product") Product product, @Param("url") String url);

    @Modifying
    @Transactional
    @Query("DELETE FROM PriceProduct pp WHERE pp.url LIKE %:url% AND pp.category = :category")
    void deleteByUrlContainingAndCategory(@Param("url") String url, @Param("category") ProductCategory category);
}
