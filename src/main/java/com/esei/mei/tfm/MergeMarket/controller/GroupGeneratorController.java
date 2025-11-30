package com.esei.mei.tfm.MergeMarket.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esei.mei.tfm.MergeMarket.constants.WebScrapingConstants;
import com.esei.mei.tfm.MergeMarket.entity.Product;
import com.esei.mei.tfm.MergeMarket.entity.ProductCategory;
import com.esei.mei.tfm.MergeMarket.entity.ProductGroup;
import com.esei.mei.tfm.MergeMarket.service.ProductCategoryService;
import com.esei.mei.tfm.MergeMarket.service.ProductService;
import com.esei.mei.tfm.MergeMarket.service.scraping.CategoryHelper;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GroupGeneratorController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private com.esei.mei.tfm.MergeMarket.dao.ProductGroupDao productGroupDao;
    
    @Autowired
    private ProductCategoryService productCategoryService;
    
    @Autowired
    private CategoryHelper categoryHelper;

    /**
     * Endpoint temporal para generar grupos de placas base desde productos existentes.
     * Ejecutar una sola vez y luego comentar o eliminar este endpoint.
     */
    @PostMapping("/admin/generate-motherboard-groups")
    public ResponseEntity<String> generateMotherboardGroups() {
        try {
            // Obtener la categoría de placas base (id = 3)
            ProductCategory category = productCategoryService.findById(WebScrapingConstants.CATEGORIA_PLACA_BASE);
            
            if (category == null) {
                return ResponseEntity.badRequest().body("Categoría de placas base no encontrada");
            }
            
            // Marcar la categoría como que usa grupos
            category.setHasGroups(true);
            // No hay método update, así que lo guardamos directamente si es necesario
            // productCategoryService.update(category);
            
            // Obtener todos los productos de placas base
            List<Product> products = productService.findByCategory(category);
            
            // Set para almacenar nombres normalizados únicos
            Set<String> uniqueNormalizedNames = new HashSet<>();
            
            // Normalizar todos los nombres y recolectar únicos
            for (Product product : products) {
                String normalizedName = categoryHelper.normaliceProductName(
                    product.getName(), 
                    product.getWeb(), 
                    category
                );
                
                // Filtrar nombres vacíos o muy cortos
                if (normalizedName != null && normalizedName.length() > 3) {
                    // Filtrar productos que no son placas base reales
                    if (!normalizedName.toLowerCase().contains("módulo") &&
                        !normalizedName.toLowerCase().contains("mikrotik") &&
                        !normalizedName.toLowerCase().contains("raspberry")) {
                        uniqueNormalizedNames.add(normalizedName);
                    }
                }
            }
            
            // Crear grupos para cada nombre normalizado único
            int createdCount = 0;
            int existingCount = 0;
            
            for (String normalizedName : uniqueNormalizedNames) {
                // Verificar si ya existe un grupo con ese nombre
                ProductGroup existing = productGroupDao.findByName(normalizedName);
                
                if (existing == null) {
                    ProductGroup group = new ProductGroup();
                    group.setName(normalizedName);
                    productGroupDao.save(group);
                    createdCount++;
                } else {
                    existingCount++;
                }
            }
            
            String message = String.format(
                "Grupos generados exitosamente. Creados: %d, Ya existían: %d, Total productos procesados: %d",
                createdCount, existingCount, products.size()
            );
            
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error al generar grupos: " + e.getMessage());
        }
    }
}
