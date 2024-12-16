CREATE TABLE IF NOT EXISTS weather_metrics
(
    `weather_metrics_id`
    int
    AUTO_INCREMENT
    PRIMARY
    KEY,
    `sensor_id`
    VARCHAR
(
    255
) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    `metric_type` VARCHAR
(
    50
) NOT NULL,
    `metric_value` DOUBLE NOT NULL,
    CONSTRAINT metric_type_check CHECK
(
    metric_type
    IN
(
    'TEMPERATURE',
    'HUMIDITY',
    'WIND_SPEED'
))
    );

CREATE INDEX idx_sensor_timestamp ON weather_metrics (sensor_id, timestamp);


