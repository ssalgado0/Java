INSERT INTO "digitalsession" (email,description, status)
SELECT v.email, v.description, v.status
FROM (VALUES ('juanpa@gmail.com', 'digital user 1 content ', 'AVAILABLE')
     , ('fperez@gmail.com', 'digital user 2 content', 'AVAILABLE')
     , ('jmgarcia@terra.es', 'digital user 3 content', 'AVAILABLE')
     , ('vidal_ccccc@gmail.com', 'digital user 4 content', 'AVAILABLE')
     , ('neil@gmail.com', 'digital user 5 content', 'AVAILABLE')
           , ('juanpa@gmail.com', 'digital user 1 empty session',
              'AVAILABLE')) AS v(email, description, status)
WHERE NOT EXISTS (SELECT 1 FROM "digitalsession");

INSERT INTO "digitalitem" (digital_session_id,description, lat, lon, link, status)
SELECT v.digital_session_id, v.description, v.lat, v.lon, v.link, v.status
FROM (VALUES (1, 'digital user 1 item 1', 10.11, 1.01, 'https://www.item1user1.net', 'AVAILABLE')
     , (1, 'digital user 1 item 2', 10.33, 1.33, 'https://www.item2user1.net', 'AVAILABLE')
     , (3, 'digital user 3 item 1', 30.11, 3.01, 'https://www.item1user3.net', 'AVAILABLE')
     , (4, 'digital user 4 item 1', 40.11, 4.01, 'https://www.item1user4.net', 'AVAILABLE')
           , (4, 'digital user 4 item 2', 40.22, 4.02, 'https://www.item2user4.net',
              'AVAILABLE')) AS v(digital_session_id, description, lat, lon, link, status)
WHERE NOT EXISTS (SELECT 1 FROM "digitalitem");