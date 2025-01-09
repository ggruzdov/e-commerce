WITH RECURSIVE generate_series AS (
    SELECT 1 as n
    UNION ALL
    SELECT n + 1 FROM generate_series WHERE n < 1000
)
INSERT INTO products(sku, name, category_id, brand, description, price, weight, attributes)
SELECT
    'LAP-' || upper(substring(brand from 1 for 3)) || '-' || lpad(n::text, 4, '0') as sku,
    brand || ' ' || series || ' ' || screen_size || '"' as name,
    2 as category_id,
    brand,
    series || ' ' || brand || ' laptop '  || 'featuring ' || processor || ' processor' as description,
    50000 + (random() * 250000)::integer as price,
    1.2 + (random() * 2.3) as weight,
    jsonb_build_object(
            'screen_size', screen_size,
            'RAM', (ARRAY[8, 16, 32, 64])[1 + (floor(random() * 4))::integer],
            'processor', processor,
            'storage_type', CASE WHEN random() > 0.5 THEN 'SSD' ELSE 'NVMe' END,
            'storage_capacity', (ARRAY[256, 512, 1024, 2048])[1 + (floor(random() * 4))::integer]
    ) as attributes
FROM (
     SELECT
         n,
         (ARRAY['Dell', 'HP', 'Lenovo', 'Asus', 'Acer', 'MSI', 'Apple', 'Microsoft', 'Samsung', 'LG'])[1 + (floor(random() * 10))::integer] as brand,
         (ARRAY[
             'Intel i3-1115G4', 'Intel i5-1135G7', 'Intel i5-12500H', 'Intel i7-1165G7', 'Intel i7-12700H', 'Intel i9-12900HK',
             'AMD Ryzen 3 5300U', 'AMD Ryzen 5 5500U', 'AMD Ryzen 5 5600H', 'AMD Ryzen 7 5700U', 'AMD Ryzen 7 5800H', 'AMD Ryzen 9 5900HX'
             ])[1 + (floor(random() * 12))::integer] as processor,
         (ARRAY[13.3, 14.0, 15.6, 16.0, 17.3])[1 + (floor(random() * 5))::integer] as screen_size,
         (ARRAY['Pro', 'Elite', 'Flex', 'Ultrabook', 'Gaming'])[1 + (floor(random() * 5))::integer] as series
     FROM generate_series
) subquery;