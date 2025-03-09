INSERT INTO attribute_definitions(category_id, display_name, name, type, values, is_optional, display_order)
VALUES
    (2, 'Screen Size', 'screen_size', 'decimal', '[13.3, 14.0, 15.6, 16.0, 17.3]', false, 1),
    (2, 'RAM', 'RAM', 'integer', '[8, 16, 32, 64]', false, 2),
    (2, 'Processor', 'processor', 'varchar', '["Intel i3-1115G4", "Intel i5-1135G7", "Intel i5-12500H", "Intel i7-1165G7", "Intel i7-12700H", "Intel i9-12900HK"]', false, 3),
    (2, 'Storage Type', 'storage_type', 'varchar', '["SSD", "NVMe"]', false, 4),
    (2, 'Storage Capacity', 'storage_capacity', 'integer', '[256, 512, 1024, 2048]', false, 5);