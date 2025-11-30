-- Paso 1: Marcar la categoría 3 (placas base) como que usa grupos
UPDATE product_category SET has_groups = 1 WHERE id = 3;

-- Verificar el cambio
SELECT id, name, has_groups FROM product_category WHERE id = 3;
