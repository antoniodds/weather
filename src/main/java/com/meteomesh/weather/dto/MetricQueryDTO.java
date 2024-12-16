package com.meteomesh.weather.dto;

import com.meteomesh.weather.entity.MetricType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MetricQueryDTO {
    private List<String> sensorIds;
    private List<MetricType> metricTypes;
    private StatisticType statisticType = StatisticType.AVERAGE;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
