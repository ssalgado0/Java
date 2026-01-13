-- $2b$10$UXm2oM219no01fmwKkZFA..EeyI0xw48zMd/wY87.7URWOLjJ1nuO ->  12345
-- $2b$10$jdLWJslYHrC0WVjE0eGYduvxwU8c0kohBAg3nzvSbiSJrbRg52twy -> admin

INSERT INTO "user" (full_Name, email, password, phone_number, role)
SELECT v.full_Name, v.email, v.password, v.phone_number, v.role
FROM (VALUES ('Juan Palomo', 'juanpa@gmail.com',
        '$2b$10$UXm2oM219no01fmwKkZFA..EeyI0xw48zMd/wY87.7URWOLjJ1nuO', 654233521, 'USER')  -- 12345
     , ('Francisco Pérez', 'fperez@gmail.com',
        '$2b$10$UXm2oM219no01fmwKkZFA..EeyI0xw48zMd/wY87.7URWOLjJ1nuO', 612446666, 'USER')  -- 12345
     , ('José Manuel García', 'jmgarcia@terra.es',
        '$2b$10$UXm2oM219no01fmwKkZFA..EeyI0xw48zMd/wY87.7URWOLjJ1nuO', 608721126, 'USER')  -- 12345
     , ('Carles Vidal', 'vidal_c@gmail.com',
        '$2b$10$jdLWJslYHrC0WVjE0eGYduvxwU8c0kohBAg3nzvSbiSJrbRg52twy', 654233521, 'ADMIN') -- admin
     , ('Nil Carbonell', 'neil@gmail.com',
        '$2b$10$jdLWJslYHrC0WVjE0eGYduvxwU8c0kohBAg3nzvSbiSJrbRg52twy', 654233521,
        'ADMIN')
           , ('Sistema externo', 'externo@externo.com',
              '$2b$10$UXm2oM219no01fmwKkZFA..EeyI0xw48zMd/wY87.7URWOLjJ1nuO', 654233521,
              'EXTERNAL')) -- external
         AS v (full_Name, email, password, phone_number, role)
WHERE NOT EXISTS (SELECT 1 FROM "user");
