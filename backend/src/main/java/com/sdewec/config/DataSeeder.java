package com.sdewec.config;

import com.sdewec.entity.*;
import com.sdewec.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Seeds the database with demo data on startup if tables are empty.
 * Provides demo credentials for testing all 9 roles.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final ShelterRepository shelterRepository;
    private final ResourceProviderRepository resourceProviderRepository;
    private final ResourceRepository resourceRepository;
    private final HazardReportRepository hazardReportRepository;
    private final WarningRepository warningRepository;
    private final IncidentRepository incidentRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Password123";

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping data seeder.");
            return;
        }
        log.info("Seeding demo data...");
        seedUsers();
        seedRescueTeams();
        seedShelters();
        seedProviders();
        seedResources();
        seedHazardReports();
        seedWarnings();
        seedIncidents();
        log.info("Demo data seeded successfully!");
        log.info("Demo credentials:");
        log.info("  citizen@test.com / Password123");
        log.info("  duty@test.com / Password123");
        log.info("  dmc@test.com / Password123");
        log.info("  district@test.com / Password123");
        log.info("  rescue@test.com / Password123");
        log.info("  shelter@test.com / Password123");
        log.info("  provider@test.com / Password123");
        log.info("  admin@test.com / Password123");
    }

    private User createUser(String name, String email, String phone, User.UserRole role) {
        return userRepository.save(User.builder()
                .name(name).email(email).phone(phone)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(role).active(true).build());
    }

    private void seedUsers() {
        // Citizens
        createUser("Kasun Perera", "citizen@test.com", "+94771234567", User.UserRole.CITIZEN);
        createUser("Nimali Fernando", "citizen2@test.com", "+94772345678", User.UserRole.CITIZEN);
        createUser("Ruwan Silva", "citizen3@test.com", "+94773456789", User.UserRole.CITIZEN);
        createUser("Priyanka Jayawardena", "citizen4@test.com", "+94774567890", User.UserRole.CITIZEN);
        createUser("Chaminda Rathnayake", "citizen5@test.com", "+94775678901", User.UserRole.CITIZEN);
        // Volunteers
        createUser("Saman Kumara", "volunteer@test.com", "+94776789012", User.UserRole.VOLUNTEER);
        createUser("Dilani Wijesinghe", "volunteer2@test.com", "+94777890123", User.UserRole.VOLUNTEER);
        // Duty Officers
        createUser("Sunil Wickramasinghe", "duty@test.com", "+94778901234", User.UserRole.DUTY_OFFICER);
        createUser("Anusha Bandara", "duty2@test.com", "+94779012345", User.UserRole.DUTY_OFFICER);
        // DMC Officers
        createUser("Mahinda Rajapaksa", "dmc@test.com", "+94770123456", User.UserRole.DMC_OFFICER);
        createUser("Thisara Samaraweera", "dmc2@test.com", "+94771234560", User.UserRole.DMC_OFFICER);
        // District Officers
        createUser("Kumari Dissanayake", "district@test.com", "+94772345601", User.UserRole.DISTRICT_OFFICER);
        createUser("Nishantha Karunaratne", "district2@test.com", "+94773456012", User.UserRole.DISTRICT_OFFICER);
        // Rescue Teams
        createUser("Alpha Rescue Team", "rescue@test.com", "+94774560123", User.UserRole.RESCUE_TEAM);
        createUser("Bravo Rescue Team", "rescue2@test.com", "+94775601234", User.UserRole.RESCUE_TEAM);
        createUser("Charlie Rescue Team", "rescue3@test.com", "+94776012345", User.UserRole.RESCUE_TEAM);
        createUser("Delta Rescue Team", "rescue4@test.com", "+94777123456", User.UserRole.RESCUE_TEAM);
        // Shelter Coordinators
        createUser("Randika Pathirana", "shelter@test.com", "+94778234567", User.UserRole.SHELTER_COORDINATOR);
        createUser("Malsha Gunasekara", "shelter2@test.com", "+94779345678", User.UserRole.SHELTER_COORDINATOR);
        createUser("Prasad Liyanage", "shelter3@test.com", "+94770456789", User.UserRole.SHELTER_COORDINATOR);
        createUser("Samanthi Herath", "shelter4@test.com", "+94771567890", User.UserRole.SHELTER_COORDINATOR);
        // Resource Providers
        createUser("Red Cross Sri Lanka", "provider@test.com", "+94772678901", User.UserRole.RESOURCE_PROVIDER);
        createUser("Lanka Hospitals Foundation", "provider2@test.com", "+94773789012", User.UserRole.RESOURCE_PROVIDER);
        createUser("Peoples Relief Trust", "provider3@test.com", "+94774890123", User.UserRole.RESOURCE_PROVIDER);
        createUser("Sri Lanka Army Welfare", "provider4@test.com", "+94775901234", User.UserRole.RESOURCE_PROVIDER);
        // Admin
        createUser("System Administrator", "admin@test.com", "+94776012345", User.UserRole.ADMIN);
    }

    private void seedRescueTeams() {
        createRescueTeam("Team Alpha - Flood Rescue", "FLOOD_RESCUE", 8, 6.9271, 79.8612, "+94711234567", "rescue@test.com");
        createRescueTeam("Team Bravo - Mountain Rescue", "MOUNTAIN_RESCUE", 6, 7.2906, 80.6337, "+94712345678", "rescue2@test.com");
        createRescueTeam("Team Charlie - Search & Rescue", "SEARCH_AND_RESCUE", 10, 6.0535, 80.2210, "+94713456789", "rescue3@test.com");
        createRescueTeam("Team Delta - Medical Rescue", "MEDICAL_RESCUE", 7, 6.9934, 79.8428, "+94714567890", "rescue4@test.com");
    }

    private void createRescueTeam(String name, String spec, int members,
                                   double lat, double lon, String phone, String userEmail) {
        userRepository.findByEmail(userEmail).ifPresent(user ->
            rescueTeamRepository.save(RescueTeam.builder()
                    .teamName(name).specialization(spec)
                    .status(RescueTeam.TeamStatus.AVAILABLE)
                    .latitude(lat).longitude(lon)
                    .contactNumber(phone).memberCount(members)
                    .user(user).build())
        );
    }

    private void seedShelters() {
        createShelter("Colombo North Shelter", "Colombo Municipal Council Ground, Colombo 01",
                6.9271, 79.8612, 500, 420, "shelter@test.com");
        createShelter("Kandy Central Shelter", "Kandy Municipal Ground, Kandy",
                7.2906, 80.6337, 300, 145, "shelter2@test.com");
        createShelter("Galle District Shelter", "Galle International Stadium, Galle",
                6.0535, 80.2210, 400, 380, "shelter3@test.com");
        createShelter("Kalutara Relief Centre", "Kalutara Town Hall, Kalutara",
                6.5854, 79.9607, 250, 60, "shelter4@test.com");
    }

    private void createShelter(String name, String location, double lat, double lon,
                                int max, int current, String coordEmail) {
        userRepository.findByEmail(coordEmail).ifPresent(coord -> {
            double pct = (double) current / max * 100;
            Shelter.ShelterStatus status = pct >= 100 ? Shelter.ShelterStatus.FULL
                    : pct >= 80 ? Shelter.ShelterStatus.NEAR_CAPACITY : Shelter.ShelterStatus.OPEN;
            shelterRepository.save(Shelter.builder()
                    .name(name).location(location)
                    .latitude(lat).longitude(lon)
                    .maximumCapacity(max).currentOccupancy(current)
                    .status(status).coordinator(coord).build());
        });
    }

    private void seedProviders() {
        createProvider("Red Cross Sri Lanka", "Dr. Kumari Wijeratne", "+94112345678",
                "No. 106, Dharmapala Mawatha, Colombo 07", 6.9147, 79.8654, "provider@test.com");
        createProvider("Lanka Hospitals Foundation", "Mr. Asela Gunasekara", "+94113456789",
                "No. 578, Elvitigala Mawatha, Colombo 05", 6.8860, 79.8634, "provider2@test.com");
        createProvider("Peoples Relief Trust", "Ms. Nilanthi Fernando", "+94114567890",
                "45 Galle Road, Kalutara", 6.5854, 79.9607, "provider3@test.com");
        createProvider("Sri Lanka Army Welfare", "Col. Bandara Rajapaksha", "+94115678901",
                "Army Headquarters, Colombo 03", 6.9037, 79.8552, "provider4@test.com");
    }

    private void createProvider(String org, String contact, String phone,
                                 String location, double lat, double lon, String userEmail) {
        userRepository.findByEmail(userEmail).ifPresent(user ->
            resourceProviderRepository.save(ResourceProvider.builder()
                    .organizationName(org).contactPerson(contact).phone(phone)
                    .location(location).latitude(lat).longitude(lon)
                    .user(user).build())
        );
    }

    private void seedResources() {
        Optional<ResourceProvider> redCross = resourceProviderRepository.findByUserId(
                userRepository.findByEmail("provider@test.com").map(User::getId).orElse(0L));
        Optional<ResourceProvider> hospitals = resourceProviderRepository.findByUserId(
                userRepository.findByEmail("provider2@test.com").map(User::getId).orElse(0L));
        Optional<ResourceProvider> peopleTrust = resourceProviderRepository.findByUserId(
                userRepository.findByEmail("provider3@test.com").map(User::getId).orElse(0L));
        Optional<ResourceProvider> army = resourceProviderRepository.findByUserId(
                userRepository.findByEmail("provider4@test.com").map(User::getId).orElse(0L));

        redCross.ifPresent(p -> {
            saveResource("DRINKING_WATER", 5000, "Litres", Resource.Priority.CRITICAL, null, p);
            saveResource("DRY_RATIONS", 2000, "Kg", Resource.Priority.HIGH,
                    java.time.LocalDate.now().plusDays(60), p);
            saveResource("INFANT_FORMULA", 300, "Kg", Resource.Priority.HIGH,
                    java.time.LocalDate.now().plusDays(30), p);
        });
        hospitals.ifPresent(p -> {
            saveResource("MEDICINES", 500, "Units", Resource.Priority.CRITICAL,
                    java.time.LocalDate.now().plusDays(180), p);
            saveResource("FIRST_AID_KITS", 200, "Kits", Resource.Priority.HIGH,
                    java.time.LocalDate.now().plusDays(365), p);
        });
        peopleTrust.ifPresent(p -> {
            saveResource("BLANKETS", 1000, "Units", Resource.Priority.MEDIUM, null, p);
            saveResource("TENTS", 150, "Units", Resource.Priority.HIGH, null, p);
            saveResource("SANITATION_KITS", 800, "Kits", Resource.Priority.MEDIUM, null, p);
        });
        army.ifPresent(p -> {
            saveResource("GENERATORS", 20, "Units", Resource.Priority.HIGH, null, p);
            saveResource("RESCUE_BOATS", 10, "Units", Resource.Priority.CRITICAL, null, p);
        });
    }

    private void saveResource(String type, int qty, String unit, Resource.Priority priority,
                               java.time.LocalDate expiry, ResourceProvider provider) {
        resourceRepository.save(Resource.builder()
                .resourceType(type).quantity(qty).unit(unit)
                .priority(priority).expiryDate(expiry).provider(provider).build());
    }

    private void seedHazardReports() {
        User citizen1 = userRepository.findByEmail("citizen@test.com").orElse(null);
        User citizen2 = userRepository.findByEmail("citizen2@test.com").orElse(null);
        User citizen3 = userRepository.findByEmail("citizen3@test.com").orElse(null);
        User volunteer = userRepository.findByEmail("volunteer@test.com").orElse(null);

        if (citizen1 != null) {
            hazardReportRepository.save(HazardReport.builder()
                    .reporter(citizen1)
                    .hazardType(HazardReport.HazardType.FLOOD)
                    .description("Heavy flooding observed near the Kelani River banks. Water level rising rapidly, several houses submerged. Residents on ground floor evacuating. Bridge road is impassable with 1.5m water.")
                    .photoUrl("https://mock.sdewec.lk/photos/flood1.jpg")
                    .latitude(6.9271).longitude(79.8612)
                    .locationName("Kelaniya, Gampaha")
                    .status(HazardReport.ReportStatus.VERIFIED)
                    .credibilityScore(90)
                    .reportedAt(LocalDateTime.now().minusHours(2))
                    .build());
        }
        if (citizen2 != null) {
            hazardReportRepository.save(HazardReport.builder()
                    .reporter(citizen2)
                    .hazardType(HazardReport.HazardType.LANDSLIDE)
                    .description("Landslide blocking main road near Kandy-Colombo highway. Large rocks and mud covering approximately 50m of road. Several vehicles trapped. No injuries reported yet.")
                    .photoUrl("https://mock.sdewec.lk/photos/landslide1.jpg")
                    .latitude(7.1000).longitude(80.3000)
                    .locationName("Kadugannawa, Kandy")
                    .status(HazardReport.ReportStatus.SUBMITTED)
                    .credibilityScore(80)
                    .reportedAt(LocalDateTime.now().minusHours(1))
                    .build());
        }
        if (citizen3 != null) {
            hazardReportRepository.save(HazardReport.builder()
                    .reporter(citizen3)
                    .hazardType(HazardReport.HazardType.CYCLONE)
                    .description("Strong winds and heavy rain. Trees falling. Power lines down in coastal areas.")
                    .latitude(6.0535).longitude(80.2210)
                    .locationName("Galle Fort Area")
                    .status(HazardReport.ReportStatus.SUBMITTED)
                    .credibilityScore(55)
                    .reportedAt(LocalDateTime.now().minusMinutes(30))
                    .build());
        }
        if (volunteer != null) {
            hazardReportRepository.save(HazardReport.builder()
                    .reporter(volunteer)
                    .hazardType(HazardReport.HazardType.FLOOD)
                    .description("Low-lying areas near Kalu River flooded. Approximately 200 families displaced. Emergency assistance needed immediately. Multiple roads blocked.")
                    .photoUrl("https://mock.sdewec.lk/photos/flood2.jpg")
                    .latitude(6.5854).longitude(79.9607)
                    .locationName("Kalutara Town")
                    .status(HazardReport.ReportStatus.UNDER_REVIEW)
                    .credibilityScore(85)
                    .reportedAt(LocalDateTime.now().minusHours(3))
                    .build());
        }
    }

    private void seedWarnings() {
        User dmcOfficer = userRepository.findByEmail("dmc@test.com").orElse(null);
        if (dmcOfficer == null) return;

        warningRepository.save(Warning.builder()
                .hazardType(HazardReport.HazardType.FLOOD)
                .warningLevel(Warning.WarningLevel.CRITICAL)
                .message("CRITICAL FLOOD WARNING: Severe flooding expected in Western Province due to heavy monsoon rains. Kelani River levels dangerously high. Immediate evacuation required for low-lying areas.")
                .emergencyInstructions("1. Evacuate immediately to higher ground.\n2. Do not walk through floodwater.\n3. Move to designated shelters.\n4. Call emergency hotline: 1938.\n5. Keep emergency kit ready.")
                .affectedArea("Western Province - Colombo, Gampaha, Kalutara")
                .affectedLatitude(6.9271).affectedLongitude(79.8612).affectedRadius(25.0)
                .createdBy(dmcOfficer).approvedBy(dmcOfficer)
                .status(Warning.WarningStatus.ACTIVE)
                .version(3)
                .createdAt(LocalDateTime.now().minusHours(4))
                .issuedAt(LocalDateTime.now().minusHours(3))
                .notificationChannels("PUSH_NOTIFICATION,SMS,AUDIBLE_ALERT")
                .build());

        warningRepository.save(Warning.builder()
                .hazardType(HazardReport.HazardType.LANDSLIDE)
                .warningLevel(Warning.WarningLevel.WARNING)
                .message("LANDSLIDE WARNING: High risk of landslides in Kandy District following 72 hours of continuous rainfall. Residents in hill areas advised to remain vigilant.")
                .emergencyInstructions("1. Stay away from steep slopes.\n2. Monitor local radio for updates.\n3. Prepare emergency kit.\n4. Report any cracks or unusual water flow.")
                .affectedArea("Kandy District - Kadugannawa, Peradeniya, Gampola")
                .affectedLatitude(7.2906).affectedLongitude(80.6337).affectedRadius(15.0)
                .createdBy(dmcOfficer)
                .status(Warning.WarningStatus.ACTIVE)
                .version(2)
                .createdAt(LocalDateTime.now().minusHours(6))
                .issuedAt(LocalDateTime.now().minusHours(5))
                .notificationChannels("PUSH_NOTIFICATION,SMS")
                .build());
    }

    private void seedIncidents() {
        User districtOfficer = userRepository.findByEmail("district@test.com").orElse(null);
        if (districtOfficer == null) return;

        incidentRepository.save(Incident.builder()
                .description("Multiple families trapped in flooded houses near Kelaniya bridge. 3 children and 2 elderly residents need immediate evacuation. Water level 1.8m.")
                .latitude(6.9271).longitude(79.8612)
                .locationName("Kelaniya, Gampaha")
                .priority(Incident.Priority.CRITICAL)
                .status(Incident.IncidentStatus.IN_PROGRESS)
                .createdBy(districtOfficer)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build());

        incidentRepository.save(Incident.builder()
                .description("Landslide blocking Colombo-Kandy main road at KM 70. Vehicles trapped. Possible injuries. Road clearance and rescue needed.")
                .latitude(7.1000).longitude(80.3000)
                .locationName("Kadugannawa, Kandy")
                .priority(Incident.Priority.HIGH)
                .status(Incident.IncidentStatus.OPEN)
                .createdBy(districtOfficer)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build());
    }
}
