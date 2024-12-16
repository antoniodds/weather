package com.meteomesh.weather.service;

import com.meteomesh.weather.dto.MetricDTO;
import com.meteomesh.weather.dto.MetricQueryDTO;
import com.meteomesh.weather.dto.MetricStatisticDTO;
import com.meteomesh.weather.dto.StatisticType;
import com.meteomesh.weather.entity.MetricType;
import com.meteomesh.weather.entity.WeatherMetric;
import com.meteomesh.weather.exception.InvalidDateRangeException;
import com.meteomesh.weather.exception.NoDataFoundException;
import com.meteomesh.weather.mapper.WeatherMetricMapper;
import com.meteomesh.weather.repository.WeatherMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherMetricServiceImpl implements IWeatherMetricService {
    private static final String NO_DATA_AVAILABLE_FOR_CALCULATION = "No data available for calculation";
    private final WeatherMetricRepository repository;
    private final WeatherMetricMapper mapper;

    @Autowired
    public WeatherMetricServiceImpl(WeatherMetricRepository repository, WeatherMetricMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WeatherMetric saveMetric(MetricDTO metricDTO) {
        WeatherMetric metric = mapper.toEntity(metricDTO);
        return repository.save(metric);
    }

    @Override
    public List<MetricStatisticDTO> queryMetrics(MetricQueryDTO queryDTO) {
        LocalDateTime endDate = queryDTO.getEndDate() != null ?
                queryDTO.getEndDate() : LocalDateTime.now();
        LocalDateTime startDate = queryDTO.getStartDate() != null ?
                queryDTO.getStartDate() : endDate.minusDays(7);

        validateDateRange(startDate, endDate);

        List<WeatherMetric> metrics = repository.findMetrics(
                queryDTO.getSensorIds(),
                queryDTO.getMetricTypes(),
                startDate,
                endDate
        );

        return calculateStatistics(metrics, queryDTO.getStatisticType());
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > 31) {
            throw new InvalidDateRangeException("Date range cannot exceed one month");
        }
    }

    private List<MetricStatisticDTO> calculateStatistics(List<WeatherMetric> metrics, StatisticType statisticType) {
        return metrics.stream()
                .collect(Collectors.groupingBy(WeatherMetric::getMetricType))
                .entrySet()
                .stream()
                .map(entry -> {
                    MetricType metricType = entry.getKey();
                    List<Double> values = entry.getValue().stream()
                            .map(WeatherMetric::getMetricValue)
                            .collect(Collectors.toList());

                    double result = switch (statisticType) {
                        case MIN -> values.stream().mapToDouble(Double::doubleValue).min()
                                .orElseThrow(() -> new NoDataFoundException(NO_DATA_AVAILABLE_FOR_CALCULATION));
                        case MAX -> values.stream().mapToDouble(Double::doubleValue).max()
                                .orElseThrow(() -> new NoDataFoundException(NO_DATA_AVAILABLE_FOR_CALCULATION));
                        case AVERAGE -> values.stream().mapToDouble(Double::doubleValue).average()
                                .orElseThrow(() -> new NoDataFoundException(NO_DATA_AVAILABLE_FOR_CALCULATION));
                        case SUM -> values.stream().mapToDouble(Double::doubleValue).sum();
                    };

                    return new MetricStatisticDTO(metricType, result, statisticType);
                })
                .collect(Collectors.toList());
    }

}