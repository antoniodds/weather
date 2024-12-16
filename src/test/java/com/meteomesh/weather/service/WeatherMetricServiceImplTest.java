package com.meteomesh.weather.service;

import com.meteomesh.weather.dto.MetricDTO;
import com.meteomesh.weather.dto.MetricQueryDTO;
import com.meteomesh.weather.dto.MetricStatisticDTO;
import com.meteomesh.weather.dto.StatisticType;
import com.meteomesh.weather.entity.MetricType;
import com.meteomesh.weather.entity.WeatherMetric;
import com.meteomesh.weather.exception.InvalidDateRangeException;
import com.meteomesh.weather.mapper.WeatherMetricMapper;
import com.meteomesh.weather.repository.WeatherMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherMetricServiceImplTest {

    @Mock
    private WeatherMetricRepository repository;

    @Mock
    private WeatherMetricMapper mapper;

    @InjectMocks
    private WeatherMetricServiceImpl service;

    private List<WeatherMetric> mockMetrics;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Create mock data
        mockMetrics = Arrays.asList(
                createWeatherMetric("sensor1", MetricType.TEMPERATURE, 20.5, now.minusHours(1)),
                createWeatherMetric("sensor1", MetricType.TEMPERATURE, 22.0, now.minusHours(2)),
                createWeatherMetric("sensor1", MetricType.HUMIDITY, 45.0, now.minusHours(1)),
                createWeatherMetric("sensor2", MetricType.TEMPERATURE, 21.0, now.minusHours(1)),
                createWeatherMetric("sensor2", MetricType.WIND_SPEED, 15.5, now.minusHours(2))
        );
    }

    @Test
    void saveMetricOK() {
        // Arrange
        MetricDTO dto = new MetricDTO();
        dto.setSensorId("sensor1");
        dto.setMetricType(MetricType.TEMPERATURE);
        dto.setMetricValue(23.5);

        WeatherMetric metric = new WeatherMetric();
        metric.setWeatherMetricsId(1);
        metric.setSensorId(dto.getSensorId());
        metric.setMetricType(dto.getMetricType());
        metric.setMetricValue(dto.getMetricValue());

        when(mapper.toEntity(any(MetricDTO.class))).thenReturn(metric);
        when(repository.save(any(WeatherMetric.class))).thenReturn(metric);

        // Act
        WeatherMetric result = service.saveMetric(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getSensorId(), result.getSensorId());
        assertEquals(dto.getMetricType(), result.getMetricType());
        assertEquals(dto.getMetricValue(), result.getMetricValue());
    }

    @Test
    void queryMetricsAverageOK() {
        // Arrange
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setSensorIds(List.of("sensor1"));
        queryDTO.setMetricTypes(List.of(MetricType.TEMPERATURE));
        queryDTO.setStatisticType(StatisticType.AVERAGE);
        queryDTO.setStartDate(now.minusDays(1));
        queryDTO.setEndDate(now);

        when(repository.findMetrics(any(), any(), any(), any()))
                .thenReturn(mockMetrics.stream()
                        .filter(m -> m.getSensorId().equals("sensor1") &&
                                m.getMetricType() == MetricType.TEMPERATURE)
                        .toList());

        // Act
        List<MetricStatisticDTO> result = service.queryMetrics(queryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(21.25, result.get(0).getValue(), 0.01); // Average of 20.5 and 22.0
    }

    @Test
    void queryMetricsAllStatistics() {
        // Arrange
        List<WeatherMetric> temperatureMetrics = Arrays.asList(
                createWeatherMetric("sensor1", MetricType.TEMPERATURE, 20.0, now),
                createWeatherMetric("sensor1", MetricType.TEMPERATURE, 25.0, now),
                createWeatherMetric("sensor1", MetricType.TEMPERATURE, 30.0, now)
        );

        when(repository.findMetrics(any(), any(), any(), any()))
                .thenReturn(temperatureMetrics);

        // Test MIN
        assertStatistic(StatisticType.MIN, 20.0);
        // Test MAX
        assertStatistic(StatisticType.MAX, 30.0);
        // Test AVERAGE
        assertStatistic(StatisticType.AVERAGE, 25.0);
        // Test SUM
        assertStatistic(StatisticType.SUM, 75.0);
    }

    @Test
    void queryMetricsInvalidDateRange() {
        // Arrange
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setStartDate(now);
        queryDTO.setEndDate(now.minusDays(1));

        // Act & Assert
        assertThrows(InvalidDateRangeException.class, () -> service.queryMetrics(queryDTO));
    }

    @Test
    void queryMetricsDateRangeExceedsOneMonth() {
        // Arrange
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setStartDate(now.minusDays(32));
        queryDTO.setEndDate(now);

        // Act & Assert
        assertThrows(InvalidDateRangeException.class, () -> service.queryMetrics(queryDTO));
    }

    @Test
    void queryMetricsNoDataFound() {
        // Arrange
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setSensorIds(List.of("nonexistent"));
        queryDTO.setStartDate(now.minusDays(1));
        queryDTO.setEndDate(now);

        when(repository.findMetrics(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        List<MetricStatisticDTO> result = service.queryMetrics(queryDTO);
        assertTrue(result.isEmpty());

    }

    private void assertStatistic(StatisticType statisticType, double expectedValue) {
        MetricQueryDTO queryDTO = new MetricQueryDTO();
        queryDTO.setStatisticType(statisticType);
        queryDTO.setStartDate(now.minusDays(1));
        queryDTO.setEndDate(now);

        List<MetricStatisticDTO> result = service.queryMetrics(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedValue, result.get(0).getValue(), 0.01);
    }

    private WeatherMetric createWeatherMetric(String sensorId, MetricType type,
                                              double value, LocalDateTime timestamp) {
        WeatherMetric metric = new WeatherMetric();
        metric.setSensorId(sensorId);
        metric.setMetricType(type);
        metric.setMetricValue(value);
        metric.setTimestamp(timestamp);
        return metric;
    }
}
