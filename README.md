# Cinema Booking - Backend
**Booking Cinema** is an online movie ticket booking system that allows users to manage bookings, theaters, select seats and payments. The backend has been completed and is built using Spring Boot with a RESTful API.

---

## Technologies Used
- **Java 21** + **Spring Boot**
- **Spring Security**
- **Spring Data JPA** (MySQL)
- **Swagger** (API documentation)
- **Cloundinary** (Management images)
- **Redis**

---

## Database Design
You can check the database schema design at at following link:
**[Booking Cinema Database Diagram](https://dbdiagram.io/d/Booking-Cinema-67bafd77263d6cf9a022109a)**

---

## How to Run the Backend 
### Clone the repository
```sh
git clone https://github.com/Vinh15088/Booking-Cinema
```
### Build the project with Maven
```sh
mvn package -Dmaven.test.skip=true
```

### Run the project using Docker
```sh
mvn package -Dtestskip
docker compose up -d api-service-0
```

### Test APIs with Swagger
Open your browser and go to:
```
http://localhost:8080/swagger-ui/index.html#/
```
