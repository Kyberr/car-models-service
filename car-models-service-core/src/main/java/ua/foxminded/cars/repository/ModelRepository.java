/*
 * Copyright 2024 Oleksandr Semenchenko
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
package ua.foxminded.cars.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.cars.repository.entity.Model;

public interface ModelRepository
    extends JpaRepository<Model, UUID>, JpaSpecificationExecutor<Model> {

  @Modifying
  @Query(
      value =
          """
    delete from model_category
    	where model_id = :modelId AND category_name = :categoryName
    """,
      nativeQuery = true)
  void removeModelFromCategory(
      @Param("modelId") UUID modelId, @Param("categoryName") String categoryName);

  @Modifying
  @Query(
      value =
          """
    insert into model_category(model_id, category_name)
    	values(:modelId, :categoryName);
    """,
      nativeQuery = true)
  void putModelToCategory(
      @Param("modelId") UUID modelId, @Param("categoryName") String categoryName);

  boolean existsByYearValue(int year);

  boolean existsByCategoriesName(String categoryName);

  boolean existsByManufacturerName(String manufacturerName);
}
