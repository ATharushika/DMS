-- ================================================
-- SDEWEC Database Seed Data
-- Smart Disaster Early-Warning & Emergency
-- Coordination System
-- Demo Data: Sri Lankan locations + demo accounts
-- ================================================

USE sdewec_db;

-- ================================================
-- USERS (password for all: Password123 -> BCrypt)
-- BCrypt hash of "Password123":
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y
-- ================================================

INSERT INTO users (name, email, phone, password, role, active, created_at) VALUES
-- Citizens (5)
('Kasun Perera', 'citizen@test.com', '+94771234567',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'CITIZEN', true, NOW()),
('Nimali Fernando', 'citizen2@test.com', '+94772345678',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'CITIZEN', true, NOW()),
('Ruwan Silva', 'citizen3@test.com', '+94773456789',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'CITIZEN', true, NOW()),
('Priyanka Jayawardena', 'citizen4@test.com', '+94774567890',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'CITIZEN', true, NOW()),
('Chaminda Rathnayake', 'citizen5@test.com', '+94775678901',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'CITIZEN', true, NOW()),

-- Volunteers (2)
('Saman Kumara', 'volunteer@test.com', '+94776789012',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'VOLUNTEER', true, NOW()),
('Dilani Wijesinghe', 'volunteer2@test.com', '+94777890123',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'VOLUNTEER', true, NOW()),

-- Duty Officers (2)
('Sunil Wickramasinghe', 'duty@test.com', '+94778901234',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DUTY_OFFICER', true, NOW()),
('Anusha Bandara', 'duty2@test.com', '+94779012345',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DUTY_OFFICER', true, NOW()),

-- DMC Officers (2)
('Mahinda Rajapaksa (DMC)', 'dmc@test.com', '+94770123456',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DMC_OFFICER', true, NOW()),
('Thisara Samaraweera (DMC)', 'dmc2@test.com', '+94771234560',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DMC_OFFICER', true, NOW()),

-- District Officers (2)
('Kumari Dissanayake', 'district@test.com', '+94772345601',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DISTRICT_OFFICER', true, NOW()),
('Nishantha Karunaratne', 'district2@test.com', '+94773456012',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'DISTRICT_OFFICER', true, NOW()),

-- Rescue Teams (4)
('Alpha Rescue Team', 'rescue@test.com', '+94774560123',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESCUE_TEAM', true, NOW()),
('Bravo Rescue Team', 'rescue2@test.com', '+94775601234',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESCUE_TEAM', true, NOW()),
('Charlie Rescue Team', 'rescue3@test.com', '+94776012345',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESCUE_TEAM', true, NOW()),
('Delta Rescue Team', 'rescue4@test.com', '+94777123456',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESCUE_TEAM', true, NOW()),

-- Shelter Coordinators (4)
('Randika Pathirana', 'shelter@test.com', '+94778234567',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'SHELTER_COORDINATOR', true, NOW()),
('Malsha Gunasekara', 'shelter2@test.com', '+94779345678',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'SHELTER_COORDINATOR', true, NOW()),
('Prasad Liyanage', 'shelter3@test.com', '+94770456789',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'SHELTER_COORDINATOR', true, NOW()),
('Samanthi Herath', 'shelter4@test.com', '+94771567890',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'SHELTER_COORDINATOR', true, NOW()),

-- Resource Providers (4)
('Red Cross Sri Lanka', 'provider@test.com', '+94772678901',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESOURCE_PROVIDER', true, NOW()),
('Lanka Hospitals Foundation', 'provider2@test.com', '+94773789012',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESOURCE_PROVIDER', true, NOW()),
('Peoples Relief Trust', 'provider3@test.com', '+94774890123',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESOURCE_PROVIDER', true, NOW()),
('Sri Lanka Army Welfare', 'provider4@test.com', '+94775901234',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'RESOURCE_PROVIDER', true, NOW()),

-- Admin (1)
('System Administrator', 'admin@test.com', '+94776012345',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'ADMIN', true, NOW());

-- ================================================
-- RESCUE TEAMS
-- ================================================

INSERT INTO rescue_teams (team_name, specialization, status, latitude, longitude, contact_number, member_count, user_id)
SELECT 'Team Alpha - Flood Rescue', 'FLOOD_RESCUE', 'AVAILABLE',
       6.9271, 79.8612, '+94711234567', 8,
       u.id FROM users u WHERE u.email = 'rescue@test.com';

INSERT INTO rescue_teams (team_name, specialization, status, latitude, longitude, contact_number, member_count, user_id)
SELECT 'Team Bravo - Mountain Rescue', 'MOUNTAIN_RESCUE', 'AVAILABLE',
       7.2906, 80.6337, '+94712345678', 6,
       u.id FROM users u WHERE u.email = 'rescue2@test.com';

INSERT INTO rescue_teams (team_name, specialization, status, latitude, longitude, contact_number, member_count, user_id)
SELECT 'Team Charlie - Search & Rescue', 'SEARCH_AND_RESCUE', 'AVAILABLE',
       6.0535, 80.2210, '+94713456789', 10,
       u.id FROM users u WHERE u.email = 'rescue3@test.com';

INSERT INTO rescue_teams (team_name, specialization, status, latitude, longitude, contact_number, member_count, user_id)
SELECT 'Team Delta - Medical Rescue', 'MEDICAL_RESCUE', 'AVAILABLE',
       6.9934, 79.8428, '+94714567890', 7,
       u.id FROM users u WHERE u.email = 'rescue4@test.com';

-- ================================================
-- SHELTERS (Sri Lankan locations)
-- ================================================

INSERT INTO shelters (name, location, latitude, longitude, maximum_capacity, current_occupancy, status, coordinator_id)
SELECT 'Colombo North Shelter', 'Colombo Municipal Council Ground, Colombo 01',
       6.9271, 79.8612, 500, 420, 'NEAR_CAPACITY',
       u.id FROM users u WHERE u.email = 'shelter@test.com';

INSERT INTO shelters (name, location, latitude, longitude, maximum_capacity, current_occupancy, status, coordinator_id)
SELECT 'Kandy Central Shelter', 'Kandy Municipal Ground, Kandy',
       7.2906, 80.6337, 300, 145, 'OPEN',
       u.id FROM users u WHERE u.email = 'shelter2@test.com';

INSERT INTO shelters (name, location, latitude, longitude, maximum_capacity, current_occupancy, status, coordinator_id)
SELECT 'Galle District Shelter', 'Galle International Stadium, Galle',
       6.0535, 80.2210, 400, 380, 'NEAR_CAPACITY',
       u.id FROM users u WHERE u.email = 'shelter3@test.com';

INSERT INTO shelters (name, location, latitude, longitude, maximum_capacity, current_occupancy, status, coordinator_id)
SELECT 'Kalutara Relief Centre', 'Kalutara Town Hall, Kalutara',
       6.5854, 79.9607, 250, 60, 'OPEN',
       u.id FROM users u WHERE u.email = 'shelter4@test.com';

-- ================================================
-- RESOURCE PROVIDERS
-- ================================================

INSERT INTO resource_providers (organization_name, contact_person, phone, location, latitude, longitude, user_id)
SELECT 'Red Cross Sri Lanka', 'Dr. Kumari Wijeratne', '+94112345678',
       'No. 106, Dharmapala Mawatha, Colombo 07',
       6.9147, 79.8654,
       u.id FROM users u WHERE u.email = 'provider@test.com';

INSERT INTO resource_providers (organization_name, contact_person, phone, location, latitude, longitude, user_id)
SELECT 'Lanka Hospitals Foundation', 'Mr. Asela Gunasekara', '+94113456789',
       'No. 578, Elvitigala Mawatha, Colombo 05',
       6.8860, 79.8634,
       u.id FROM users u WHERE u.email = 'provider2@test.com';

INSERT INTO resource_providers (organization_name, contact_person, phone, location, latitude, longitude, user_id)
SELECT 'Peoples Relief Trust', 'Ms. Nilanthi Fernando', '+94114567890',
       '45 Galle Road, Kalutara',
       6.5854, 79.9607,
       u.id FROM users u WHERE u.email = 'provider3@test.com';

INSERT INTO resource_providers (organization_name, contact_person, phone, location, latitude, longitude, user_id)
SELECT 'Sri Lanka Army Welfare', 'Col. Bandara Rajapaksha', '+94115678901',
       'Army Headquarters, Colombo 03',
       6.9037, 79.8552,
       u.id FROM users u WHERE u.email = 'provider4@test.com';

-- ================================================
-- RESOURCES (10 resources)
-- ================================================

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'DRINKING_WATER', 5000, 'Litres', 'CRITICAL', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Red Cross Sri Lanka';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'DRY_RATIONS', 2000, 'Kg', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 60 DAY),
       p.id FROM resource_providers p WHERE p.organization_name = 'Red Cross Sri Lanka';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'MEDICINES', 500, 'Units', 'CRITICAL', DATE_ADD(CURDATE(), INTERVAL 180 DAY),
       p.id FROM resource_providers p WHERE p.organization_name = 'Lanka Hospitals Foundation';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'FIRST_AID_KITS', 200, 'Kits', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 365 DAY),
       p.id FROM resource_providers p WHERE p.organization_name = 'Lanka Hospitals Foundation';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'BLANKETS', 1000, 'Units', 'MEDIUM', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Peoples Relief Trust';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'TENTS', 150, 'Units', 'HIGH', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Peoples Relief Trust';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'GENERATORS', 20, 'Units', 'HIGH', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Sri Lanka Army Welfare';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'INFANT_FORMULA', 300, 'Kg', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 30 DAY),
       p.id FROM resource_providers p WHERE p.organization_name = 'Red Cross Sri Lanka';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'SANITATION_KITS', 800, 'Kits', 'MEDIUM', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Peoples Relief Trust';

INSERT INTO resources (resource_type, quantity, unit, priority, expiry_date, provider_id)
SELECT 'RESCUE_BOATS', 10, 'Units', 'CRITICAL', NULL,
       p.id FROM resource_providers p WHERE p.organization_name = 'Sri Lanka Army Welfare';

-- ================================================
-- HAZARD REPORTS (demo data)
-- ================================================

INSERT INTO hazard_reports (reporter_id, hazard_type, description, photo_url, latitude, longitude, location_name, reported_at, status, credibility_score)
SELECT u.id, 'FLOOD',
       'Heavy flooding observed near the Kelani River banks. Water level rising rapidly, several houses submerged. Residents on ground floor evacuating. Bridge road is impassable with 1.5m water.',
       'https://mock.sdewec.lk/photos/flood1.jpg',
       6.9271, 79.8612, 'Kelaniya, Gampaha',
       DATE_SUB(NOW(), INTERVAL 2 HOUR), 'VERIFIED', 90
FROM users u WHERE u.email = 'citizen@test.com';

INSERT INTO hazard_reports (reporter_id, hazard_type, description, photo_url, latitude, longitude, location_name, reported_at, status, credibility_score)
SELECT u.id, 'LANDSLIDE',
       'Landslide blocking main road near Kandy-Colombo highway. Large rocks and mud covering approximately 50m of road. Several vehicles trapped. No injuries reported yet but situation dangerous.',
       'https://mock.sdewec.lk/photos/landslide1.jpg',
       7.1000, 80.3000, 'Kadugannawa, Kandy',
       DATE_SUB(NOW(), INTERVAL 1 HOUR), 'SUBMITTED', 80
FROM users u WHERE u.email = 'citizen2@test.com';

INSERT INTO hazard_reports (reporter_id, hazard_type, description, photo_url, latitude, longitude, location_name, reported_at, status, credibility_score)
SELECT u.id, 'CYCLONE',
       'Strong winds and heavy rain. Trees falling. Power lines down. Coastal areas severely affected.',
       NULL,
       6.0535, 80.2210, 'Galle Fort Area',
       DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'SUBMITTED', 55
FROM users u WHERE u.email = 'citizen3@test.com';

INSERT INTO hazard_reports (reporter_id, hazard_type, description, photo_url, latitude, longitude, location_name, reported_at, status, credibility_score)
SELECT u.id, 'FLOOD',
       'Low-lying areas near Kalu River flooded. Approximately 200 families displaced. Emergency assistance needed immediately.',
       'https://mock.sdewec.lk/photos/flood2.jpg',
       6.5854, 79.9607, 'Kalutara Town',
       DATE_SUB(NOW(), INTERVAL 3 HOUR), 'UNDER_REVIEW', 85
FROM users u WHERE u.email = 'volunteer@test.com';

-- ================================================
-- WARNINGS (demo data)
-- ================================================

INSERT INTO warnings (hazard_type, warning_level, message, emergency_instructions, affected_area,
                      affected_latitude, affected_longitude, affected_radius,
                      created_by_id, status, version, created_at, issued_at, notification_channels)
SELECT 'FLOOD', 'CRITICAL',
       'CRITICAL FLOOD WARNING: Severe flooding expected in Western Province due to heavy monsoon rains. Kelani River levels dangerously high. Immediate evacuation required for low-lying areas.',
       '1. Evacuate immediately to higher ground.\n2. Do not walk through floodwater.\n3. Move to designated shelters.\n4. Call emergency hotline: 1938.\n5. Keep emergency kit ready.',
       'Western Province - Colombo, Gampaha, Kalutara',
       6.9271, 79.8612, 25.0,
       u.id, 'ACTIVE', 3, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR),
       'PUSH_NOTIFICATION,SMS,AUDIBLE_ALERT'
FROM users u WHERE u.email = 'dmc@test.com';

INSERT INTO warnings (hazard_type, warning_level, message, emergency_instructions, affected_area,
                      affected_latitude, affected_longitude, affected_radius,
                      created_by_id, status, version, created_at, issued_at, notification_channels)
SELECT 'LANDSLIDE', 'WARNING',
       'LANDSLIDE WARNING: High risk of landslides in Kandy District following 72 hours of continuous rainfall. Residents in hill areas advised to remain vigilant.',
       '1. Stay away from steep slopes.\n2. Monitor local radio for updates.\n3. Prepare emergency kit.\n4. Report any cracks or unusual water flow.',
       'Kandy District - Kadugannawa, Peradeniya, Gampola',
       7.2906, 80.6337, 15.0,
       u.id, 'ACTIVE', 2, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR),
       'PUSH_NOTIFICATION,SMS'
FROM users u WHERE u.email = 'dmc@test.com';

-- ================================================
-- INCIDENTS
-- ================================================

INSERT INTO incidents (description, latitude, longitude, location_name, priority, status, created_by_id, created_at)
SELECT 'Multiple families trapped in flooded houses near Kelaniya bridge. 3 children and 2 elderly residents need immediate evacuation. Water level 1.8m.',
       6.9271, 79.8612, 'Kelaniya, Gampaha',
       'CRITICAL', 'IN_PROGRESS',
       u.id, DATE_SUB(NOW(), INTERVAL 2 HOUR)
FROM users u WHERE u.email = 'district@test.com';

INSERT INTO incidents (description, latitude, longitude, location_name, priority, status, created_by_id, created_at)
SELECT 'Landslide blocking Colombo-Kandy main road at KM 70. Vehicles trapped. Possible injuries. Road clearance and rescue needed.',
       7.1000, 80.3000, 'Kadugannawa, Kandy',
       'HIGH', 'OPEN',
       u.id, DATE_SUB(NOW(), INTERVAL 1 HOUR)
FROM users u WHERE u.email = 'district@test.com';

COMMIT;
