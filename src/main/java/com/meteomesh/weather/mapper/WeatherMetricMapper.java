package com.meteomesh.weather.mapper;

import com.meteomesh.weather.dto.MetricDTO;
import com.meteomesh.weather.entity.WeatherMetric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WeatherMetricMapper {

    @Mapping(target = "weatherMetricsId", ignore = true)
    WeatherMetric toEntity(MetricDTO dto);

    MetricDTO toDto(WeatherMetric entity);

    List<MetricDTO> toDtoList(List<WeatherMetric> entities);
}
