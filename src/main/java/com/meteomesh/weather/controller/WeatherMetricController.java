package com.meteomesh.weather.controller;

import com.meteomesh.weather.constants.ApiConstants;
import com.meteomesh.weather.dto.MetricDTO;
import com.meteomesh.weather.dto.MetricQueryDTO;
import com.meteomesh.weather.dto.MetricStatisticDTO;
import com.meteomesh.weather.entity.WeatherMetric;
import com.meteomesh.weather.service.IWeatherMetricService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.API_V1)
public class WeatherMetricController {

    private final IWeatherMetricService service;

    @Autowired
    public WeatherMetricController(IWeatherMetricService service) {
        this.service = service;
    }

    @PostMapping(ApiConstants.METRICS)
    public ResponseEntity<WeatherMetric> saveMetric(@Valid @RequestBody MetricDTO metricDTO) {
        return ResponseEntity.ok(service.saveMetric(metricDTO));
    }

    @GetMapping(ApiConstants.METRICS)
    public ResponseEntity<List<MetricStatisticDTO>> queryMetrics(
            @Valid @RequestBody MetricQueryDTO queryDTO) {
        return ResponseEntity.ok(service.queryMetrics(queryDTO));
    }
}
