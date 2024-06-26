package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.time.Year;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.service.dto.ModelYearDto;

@Mapper(componentModel = SPRING)
public interface ModelYearMapper {

  default Integer toInteger(Year year) {
    return year.getValue();
  }

  default Integer toInteger(ModelYear modelYear) {
    Year year = modelYear.getValue();
    return year.getValue();
  }

  default Year toYear(Integer year) {
    return Year.of(year);
  }

  @Mapping(target = "value", source = "year")
  ModelYear toEntity(Integer year);

  @Mapping(target = "value", source = "year")
  ModelYear toEntity(ModelYearDto modelYearDto);

  @InheritInverseConfiguration
  ModelYearDto toDto(ModelYear modelYear);
}
