INSERT INTO categories(name, parent_id)
VALUES
    ('Digital equipment', null),
    ('Laptops', 1),
    ('Home appliances', null),
    ('Washing machines', 3);

-- Laptop

INSERT INTO products(sku, name, category_id, description, price, weight)
VALUES
    ('LAP-DELL-XPS15', 'Dell XPS 15', 2, 'High-performance laptop...', 150650, 2.2);

INSERT INTO attribute_definitions (category_id, name, type, is_required, display_order)
VALUES
    (2, 'Screen Size', 'decimal', true, 1),
    (2, 'RAM', 'integer', true, 2),
    (2, 'Processor', 'varchar', true, 3),
    (2, 'Storage Type', 'varchar', true, 4),
    (2, 'Storage Capacity', 'integer', true, 5);

INSERT INTO product_attributes (product_id, attribute_def_id, value)
VALUES
    (1, 1, '15.6'),
    (1, 2, '16'),
    (1, 3, 'Intel i7-12700H'),
    (1, 4, 'SSD'),
    (1, 5, '512');

-- Washing machine
INSERT INTO products(sku, name, category_id, description, price, weight)
VALUES
    ('WM-SAMSG-8500', 'Samsung EcoBubble 8500', 4, 'Eco Wash', 300650, 30);

INSERT INTO attribute_definitions (category_id, name, type, is_required, display_order)
VALUES
    (4, 'Capacity', 'decimal', true, 1),
    (4, 'Spin Speed', 'integer', true, 2),
    (4, 'Energy Rating', 'varchar', true, 3),
    (4, 'Loading Type', 'varchar', true, 4);

INSERT INTO product_attributes (product_id, attribute_def_id, value)
VALUES
    (2, 6, '8.0'),
    (2, 7, '1400'),
    (2, 8, 'A+++'),
    (2, 9, 'Front Load');