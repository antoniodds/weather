package com.meteomesh.weather.dto;

import com.meteomesh.weather.entity.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricDTO {
    @NotBlank(message = "Sensor ID is required")
    private String sensorId;

    @NotNull(message = "Metric type is required")
    private MetricType metricType;

    @NotNull(message = "Value is required")
    private Double metricValue;

    private LocalDateTime timestamp = LocalDateTime.now();
}