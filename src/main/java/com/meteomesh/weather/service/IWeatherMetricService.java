package com.meteomesh.weather.service;

import com.meteomesh.weather.dto.MetricDTO;
import com.meteomesh.weather.dto.MetricQueryDTO;
import com.meteomesh.weather.dto.MetricStatisticDTO;
import com.meteomesh.weather.entity.WeatherMetric;

import java.util.List;

public interface IWeatherMetricService {
    WeatherMetric saveMetric(MetricDTO metricDTO);

    List<MetricStatisticDTO> queryMetrics(MetricQueryDTO queryDTO);
}

