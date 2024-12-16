package com.meteomesh.weather.repository;

import com.meteomesh.weather.entity.MetricType;
import com.meteomesh.weather.entity.WeatherMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherMetricRepository extends JpaRepository<WeatherMetric, Long> {
    @Query("SELECT w FROM WeatherMetric w WHERE " +
            "(:sensorIds IS NULL OR w.sensorId IN :sensorIds) AND " +
            "(:metricTypes IS NULL OR w.metricType IN :metricTypes) AND " +
            "w.timestamp BETWEEN :startDate AND :endDate")
    List<WeatherMetric> findMetrics(
            List<String> sensorIds,
            List<MetricType> metricTypes,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}