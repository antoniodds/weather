package com.meteomesh.weather.dto;

import com.meteomesh.weather.entity.MetricType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricStatisticDTO {
    private MetricType metricType;
    private double value;
    private StatisticType statisticType;
}




