INSERT INTO category (name, description, parent_id)
SELECT v.name, v.description, v.parent_id
FROM (VALUES ('Cámaras', 'Cámaras', NULL),
             ('Cámaras de Fotos', 'Cámaras de Fotos', 1),
             ('Cámaras de Video', 'Cámaras de Video', 1),
             ('Iluminación', 'Todo tipo de accesorios de iluminación', NULL),
             ('Focos', 'Focos direccionales, omnidireccionales, etc.', 4),
             ('Difusores', 'Difusores para focos', 4)) AS v(name, description, parent_id)
WHERE NOT EXISTS (SELECT 1 FROM category);

INSERT INTO product (name, description, daily_price, brand, model, category_id)
SELECT v.name, v.description, v.daily_price, v.brand, v.model, v.category_id
FROM (VALUES ('Canon 500D', 'Cámara de fotos Canon 500D', 100, 'Canon', '500D', 2),
             ('Canon EOS R8', 'Cámara de fotos Canon EOS R8', 200, 'Canon', 'EOS R8', 2),
             ('Canon EOS R5 C', 'Cámara de video Canon EOS R5 C', 250, 'Canon', 'EOS R5 C', 3),
             ('Foco Phillips 120L', 'Foco LED Phillips de luz blanca 120W. Sin difusor', 80,
              'Phillips', '120L', 5),
             ('Foco Phillips 220L', 'Foco LED Phillips de luz blanca 220W. Sin difusor', 120,
              'Phillips', '220L', 5),
             ('Difusor Universal 3000', 'Difusor universal para focos de hasta 3000 lumens', 30,
              'Generic', 'DU3000', 6),
             ('Trípode Manfrotto 055XPROB', 'Trípode profesional Manfrotto 055XPROB', 50,
              'Manfrotto', '055XPROB',
              1)) AS v(name, description, daily_price, brand, model, category_id)
WHERE NOT EXISTS (SELECT 1 FROM product);

INSERT INTO item (serial_number, status, product_id)
SELECT v.serial_number, v.status, v.product_id
FROM (VALUES ('SN-C500D-001', 'OPERATIONAL', 1),
             ('SN-C500D-002', 'OPERATIONAL', 1),
             ('SN-C500D-003', 'NON_OPERATIONAL', 1),
             ('SN-EOSR8-001', 'OPERATIONAL', 2),
             ('SN-EOSR8-002', 'OPERATIONAL', 2),
             ('SN-EOSR5C-001', 'OPERATIONAL', 3),
             ('SN-FP120L-001', 'OPERATIONAL', 4),
             ('SN-FP120L-002', 'NON_OPERATIONAL', 4),
             ('SN-FP220L-001', 'OPERATIONAL', 5),
             ('SN-DU3000-001', 'NON_OPERATIONAL', 6)) AS v(serial_number, status, product_id)
WHERE NOT EXISTS (SELECT 1 FROM item);

INSERT INTO booking(start_date, end_date, status, user_id)
SELECT v.start_date, v.end_date, v.status, v.user_id
FROM (VALUES (current_date, current_date + 5, 'PENDING', 5),
             (current_date, current_date + 10, 'CANCELLED', 5),
             (current_date + 7, current_date + 14, 'PENDING',
              5)) AS v(start_date, end_date, status, user_id)
WHERE NOT EXISTS (SELECT 1 FROM booking);

INSERT INTO booking_line(booking_id, product_id, quantity, price_per_unit)
SELECT v.booking_id, v.product_id, v.quantity, v.price_per_unit
FROM (VALUES (1, 1, 2, 100),
             (1, 2, 1, 200),
             (2, 2, 1, 200),
             (3, 4, 1, 80),
             (3, 5, 1, 120)) AS v(booking_id, product_id, quantity, price_per_unit)
WHERE NOT EXISTS (SELECT 1 FROM booking_line);