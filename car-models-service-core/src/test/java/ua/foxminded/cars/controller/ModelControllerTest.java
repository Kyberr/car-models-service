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
package ua.foxminded.cars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.cars.service.dto.ModelDto;
import ua.foxminded.cars.service.imp.ModelServiceImp;

@WebMvcTest(controllers = ModelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModelControllerTest {

  private static final String MODEL_NAME = "A7";
  private static final int MODEL_YEAR = 2020;
  private static final UUID MODEL_ID = UUID.randomUUID();
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String CATEGORY_NAME = "Sedan";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @MockBean private ModelServiceImp modelService;

  private ModelDto modelDto;
  private String modelDtoJson;

  //  @BeforeEach
  //  void setUp() {
  //    modelDto =
  //        ModelDto.builder()
  //            .id(MODEL_ID)
  //            .year(MODEL_YEAR)
  //            .manufacturer(MANUFACTURER_NAME)
  //            .name(MODEL_NAME)
  //            .categories(Set.of(CATEGORY_NAME))
  //            .build();
  //  }
  //
  //  @Test
  //  void getByManufacturerAndModelAndYear_ShouldReturnStatus400_WhenConstraintViolationException()
  //      throws Exception {
  //    String notValidYear = "-2023";
  //
  //    mockMvc
  //        .perform(
  //            get(
  //                "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
  //                MANUFACTURER_NAME,
  //                MODEL_NAME,
  //                notValidYear))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void search_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
  //    String notValidYear = "-2023";
  //
  //    mockMvc
  //        .perform(
  //            get("/v1/models")
  //                .param("maxYear", notValidYear)
  //                .param("minYear", notValidYear)
  //                .param("year", notValidYear))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void create_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
  //    doThrow(AlreadyExistsException.class).when(modelService).createModel(modelDto);
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            post(
  //                    "/v1/manufacturers/{manufacturers}/models/{modle}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    MODEL_YEAR,
  //                    MODEL_ID)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isConflict());
  //  }
  //
  //  @Test
  //  void create_ShouldReturnStatus400_WhenConstrainViolationException() throws Exception {
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //    int notValidYear = -2023;
  //
  //    mockMvc
  //        .perform(
  //            put(
  //                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    notValidYear,
  //                    MODEL_ID)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void create_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
  //    modelDto.setCategories(null);
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            put(
  //                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    MODEL_YEAR,
  //                    MODEL_ID)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void update_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
  //    doThrow(NotFoundException.class).when(modelService).updateModelPartly(modelDto);
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            put(
  //                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    MODEL_YEAR)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isNotFound());
  //  }
  //
  //  @Test
  //  void update_ShouldReturnStatus400_WhenMethodArgumenNotValidException() throws Exception {
  //    modelDto.setCategories(null);
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            put(
  //                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    MODEL_YEAR)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void update_ShouldReturnStatus400_WhenConstraintViolationException() throws Exception {
  //    int notValidYear = -2023;
  //    modelDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            put(
  //                    "/v1/manufacturers/{manufacturers}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    notValidYear)
  //                .contentType(APPLICATION_JSON)
  //                .content(modelDtoJson))
  //        .andExpect(status().isBadRequest());
  //  }
  //
  //  @Test
  //  void deleteById_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
  //    doThrow(NotFoundException.class).when(modelService).deleteModelById(MODEL_ID);
  //
  //    mockMvc.perform(delete("/v1/models/{id}", MODEL_ID)).andExpect(status().isNotFound());
  //  }
}
