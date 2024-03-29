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
package ua.com.foxminded.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.controller.CategoryControllerIntegrationTest.CATEGORY_NAME;
import static ua.com.foxminded.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;
import static ua.com.foxminded.controller.ModelControllerIntegrationTest.MODEL_ID;
import static ua.com.foxminded.controller.ModelControllerIntegrationTest.MODEL_YEAR;
import static ua.com.foxminded.controller.ModelNameControllerIntegrationTest.MODEL_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.dto.ModelDto;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.service.ModelService;

@WebMvcTest(controllers = ModelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModelControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @MockBean private ModelService modelService;

  private ModelDto modelDto;
  private String modelDtoJson;

  @BeforeEach
  void setUp() {
    modelDto =
        ModelDto.builder()
            .id(MODEL_ID)
            .year(MODEL_YEAR)
            .manufacturer(MANUFACTURER_NAME)
            .name(MODEL_NAME)
            .categories(Set.of(CATEGORY_NAME))
            .build();
  }

  @Test
  void getByManufacturerAndModelAndYear_ShouldReturnStatus400_WhenConstraintViolationException()
      throws Exception {
    String notValidYear = "-2023";

    mockMvc
        .perform(
            get(
                "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
                MANUFACTURER_NAME,
                MODEL_NAME,
                notValidYear))
        .andExpect(status().isBadRequest());
  }

  @Test
  void search_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
    String notValidYear = "-2023";

    mockMvc
        .perform(
            get("/v1/models")
                .param("maxYear", notValidYear)
                .param("minYear", notValidYear)
                .param("year", notValidYear))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
    doThrow(AlreadyExistsException.class).when(modelService).create(modelDto);
    modelDtoJson = mapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            post(
                    "/v1/manufacturers/{manufacturers}/models/{modle}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    MODEL_YEAR,
                    MODEL_ID)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isConflict());
  }

  @Test
  void create_ShouldReturnStatus400_WhenConstrainViolationException() throws Exception {
    modelDtoJson = mapper.writeValueAsString(modelDto);
    int notValidYear = -2023;

    mockMvc
        .perform(
            put(
                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    notValidYear,
                    MODEL_ID)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
    modelDto.setCategories(null);
    modelDtoJson = mapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(
                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    MODEL_YEAR,
                    MODEL_ID)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
    doThrow(NotFoundException.class).when(modelService).update(modelDto);
    modelDtoJson = mapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(
                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    MODEL_YEAR)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_ShouldReturnStatus400_WhenMethodArgumenNotValidException() throws Exception {
    modelDto.setCategories(null);
    modelDtoJson = mapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(
                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    MODEL_YEAR)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_ShouldReturnStatus400_WhenConstraintViolationException() throws Exception {
    int notValidYear = -2023;
    modelDtoJson = mapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            put(
                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
                    MANUFACTURER_NAME,
                    MODEL_NAME,
                    notValidYear)
                .contentType(APPLICATION_JSON)
                .content(modelDtoJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deleteById_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
    doThrow(NotFoundException.class).when(modelService).deleteById(MODEL_ID);

    mockMvc.perform(delete("/v1/models/{id}", MODEL_ID)).andExpect(status().isNotFound());
  }
}
