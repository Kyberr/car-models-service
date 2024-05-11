/*
 * Copyright 2023 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.foxminded.cars.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.dto.CategoryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

  Set<CategoryDto> toDtoSet(List<Category> categories);

  CategoryDto toDto(Category category);

  @Mapping(target = "name", source = "category")
  Category stringToEntity(String category);

  default String entityToString(Category category) {
    return category.getName();
  }

  Set<String> categoriesToSet(Set<Category> categories);
}