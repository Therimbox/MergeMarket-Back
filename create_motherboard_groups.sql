-- Script para crear grupos de placas base (categoría 3)
-- Paso 1: Marcar la categoría 3 como que usa grupos
UPDATE product_category SET has_groups = 1 WHERE id = 3;

-- Paso 2: Insertar grupos únicos desde los productos existentes
-- IMPORTANTE: Este script asume que ya has actualizado CategoryHelper.java con la nueva normalización

-- Crear una tabla temporal con los nombres normalizados
CREATE TEMPORARY TABLE temp_motherboard_groups AS
SELECT DISTINCT
    -- Normalización manual similar a la lógica de Java
    TRIM(REGEXP_REPLACE(
        REGEXP_REPLACE(
            REGEXP_REPLACE(
                REGEXP_REPLACE(
                    REGEXP_REPLACE(
                        REGEXP_REPLACE(
                            REGEXP_REPLACE(
                                REGEXP_REPLACE(
                                    REGEXP_REPLACE(
                                        REGEXP_REPLACE(
                                            REGEXP_REPLACE(
                                                REGEXP_REPLACE(
                                                    REGEXP_REPLACE(
                                                        REGEXP_REPLACE(
                                                            REGEXP_REPLACE(
                                                                REGEXP_REPLACE(
                                                                    REGEXP_REPLACE(
                                                                        REGEXP_REPLACE(
                                                                            name,
                                                                            'Placa Base ', '', 1, 0, 'i'
                                                                        ),
                                                                        'Livemixer', 'Live Mixer', 1, 0, 'i'
                                                                    ),
                                                                    'Ligthning', 'Lightning', 1, 0, 'i'
                                                                ),
                                                                ' (Intel|Amd) (A520|A620|A320|B450|B550|B650|B650e|B650m|B660|B760|B840|B850|B860|X570|X670|X670e|X870|X870e|H410|H510|H610|H770|H810|Z490|Z590|Z690|Z790|Z890|Q570|Q670|Q870|W680|W790|W880|C256|C262|C266|C741|Trx50|Wrx80|Wrx90|X299|Epyc|Rome|Turin|Sienad8ud3)', '', 1, 0, 'i'
                                                            ),
                                                            ' (Lga ?[0-9]+|Am4|Am5|Str5|Sp3|Sp5|Sp6|Swrx8|Socket [A-Za-z0-9]+)', '', 1, 0, 'i'
                                                        ),
                                                        ' (Atx|Micro ?Atx|Mini ?Itx|E-Atx|Eeb|Ceb|Ssi ?Ceb|Micro-Atx|Mini-Itx)', '', 1, 0, 'i'
                                                    ),
                                                    ' (Ddr3|Ddr4|Ddr5|D5)', '', 1, 0, 'i'
                                                ),
                                                ' (Wifi ?[0-9]?|Wi-Fi ?[0-9]?|Ax|Ac|Bluetooth ?[0-9.]+)', '', 1, 0, 'i'
                                            ),
                                            ' (/M\\.2\\+?|M\\.2\\+?|Dual M\\.2)', '', 1, 0, 'i'
                                        ),
                                        ' (V2|V3|R2\\.0|Ii|Iii|2\\.0|3\\.0|\\(.*?\\)) *$', '', 1, 0, 'i'
                                    ),
                                    ' (Argb|Rgb|Ice|White|Blanca|Black|Se|Btf|Plus)', '', 1, 0, 'i'
                                ),
                                ' (\\+|Pcie ?[0-9.]+|Usb ?[0-9.]+|Gen ?[0-9]+|Thunderbolt ?[0-9]+|Lan|Gbe|[0-9]+gbe|[0-9]+\\.?[0-9]*g ?Lan)', '', 1, 0, 'i'
                            ),
                            ' ([0-9]+gb|[0-9]+tb|[0-9]+ ?Dimm|Ecc|Raid|Ipmi|Ast[0-9]+)', '', 1, 0, 'i'
                        ),
                        ' (Sata ?Iii?|Nvme|Soporta|Compatibilidad|Avanzada|Robusta|Server|Gaming|Creatividad|Audio|Uefi|Overclocking)', '', 1, 0, 'i'
                    ),
                    '[-/]+$', '', 1, 0
                ),
                '  +', ' ', 1, 0, 'g'
            )
        )
    ) AS normalized_name
FROM product
WHERE category_id = 3
    AND name NOT LIKE '%Módulo Tpm%'
    AND name NOT LIKE '%Mikrotik%'
    AND name NOT LIKE '%Raspberry%';

-- Insertar los grupos únicos en product_group
INSERT INTO product_group (name)
SELECT DISTINCT normalized_name
FROM temp_motherboard_groups
WHERE normalized_name IS NOT NULL
    AND LENGTH(normalized_name) > 3
    AND normalized_name NOT IN (SELECT name FROM product_group)
ORDER BY normalized_name;

-- Limpiar tabla temporal
DROP TEMPORARY TABLE temp_motherboard_groups;

-- Paso 3: Verificar cuántos grupos se crearon
SELECT COUNT(*) as total_groups FROM product_group;

-- Paso 4: Mostrar algunos ejemplos de grupos creados
SELECT name FROM product_group ORDER BY name LIMIT 50;

-- NOTA IMPORTANTE:
-- Después de ejecutar este script, debes:
-- 1. Ejecutar el scraping para la categoría 3 para que se asignen los productos a los grupos
-- 2. O ejecutar un UPDATE manual para asignar los productos existentes a sus grupos correspondientes
