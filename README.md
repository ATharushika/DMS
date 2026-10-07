# Smart Disaster Early-Warning & Emergency Coordination System (SDEWEC)

This is a comprehensive full-stack application designed to coordinate disaster warnings and emergency response efforts. It includes a Spring Boot backend and a React Native mobile application.

## Technologies Used

### Backend
* Java 21
* Spring Boot 3.2.x
* Spring Data JPA & Hibernate
* Spring Security (JWT based Authentication)
* MySQL Database
* Maven

### Frontend
* React Native (Expo)
* React Navigation (Stack & Bottom Tabs)
* Axios for API communication
* AsyncStorage for session persistence

## Getting Started

### 1. Database Setup
1. Ensure MySQL is installed and running on your machine.
2. The `database/seed.sql` file contains the complete schema and demo data. You can execute it to prepopulate the database, but it's optional as Hibernate will create the tables automatically (`createDatabaseIfNotExist=true` is set).
3. The default credentials expected in `application.properties` are `root` for both username and password on `localhost:3306`. If your credentials differ, update `backend/src/main/resources/application.properties`.

### 2. Running the Backend
A Maven Wrapper is included so you do not need Maven installed on your machine.

From the `backend` directory, run:
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.
*Note: A `DataSeeder` class will automatically populate the database with users, shelters, rescue teams, resources, and demo data if the tables are empty.*

### 3. Running the Mobile App
From the `mobile` directory, ensure dependencies are installed, then start Expo:

```bash
npm install
npm start
```
You can then run it on an Android Emulator by pressing `a`, or test on a physical device using the Expo Go app. 

*Note: In `mobile/src/context/AuthContext.js`, the `API_URL` is configured as `http://10.0.2.2:8080/api` which is standard for Android Emulators. If testing on a physical device on your local Wi-Fi, change this to your computer's IP address (e.g. `http://192.168.1.X:8080/api`).*

## Demo Accounts

All accounts use the password: `Password123`

* Citizen: `citizen@test.com`
* Volunteer: `volunteer@test.com`
* Duty Officer: `duty@test.com`
* DMC Officer: `dmc@test.com`
* District Officer: `district@test.com`
* Rescue Team: `rescue@test.com`
* Shelter Coordinator: `shelter@test.com`
* Resource Provider: `provider@test.com`
* System Admin: `admin@test.com`

## Implemented Use Cases
1. **Issue and Broadcast Disaster Warning** (DMC Officer)
2. **Submit and Verify Ground Hazard Report** (Citizen/Volunteer & Duty Officer)
3. **Coordinate Emergency Response and Rescue** (District Officer & Rescue Teams)
4. **Coordinate Relief Resources and Emergency Shelters** (District Officer, Providers & Shelters)
