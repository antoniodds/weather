# Weather Sensor API

A Spring Boot REST API for processing and querying weather sensor data.

## Requirements

- Java 21
- Maven 3.8+
- Docker (optional)

## Building and Running

### Using Maven

```bash
mvn clean install
java -jar target/weather-0.0.1-SNAPSHOT.jar
```

### Using Docker

```bash
docker build -t weather-sensor-api .
docker run -p 8080:8080 weather-sensor-api
```

## API Endpoints

POST /api/v1/metrics
Submit new metric data from sensors.

GET /api/v1/metrics
Query metric data with various filters and statistics.

