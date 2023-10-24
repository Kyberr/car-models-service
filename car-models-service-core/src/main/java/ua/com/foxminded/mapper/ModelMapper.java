package ua.com.foxminded.mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.dto.ModelDto;
import ua.com.foxminded.entity.Model;

@Mapper(nullValueCheckStrategy = ALWAYS,  
        componentModel = MappingConstants.ComponentModel.SPRING, 
        uses = {CategoryMapper.class, ModelNameMapper.class, ManufacturerMapper.class})
public interface ModelMapper {
    
    @Mapping(target = "name", source = "modelName")
    ModelDto map(Model model);
    
    @InheritInverseConfiguration
    Model map(ModelDto modelDto);
}