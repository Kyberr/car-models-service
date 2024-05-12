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
package ua.foxminded.cars.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.service.dto.ModelDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ModelControllerIntegrationTest {

  private static final String MODEL_ID_PATH = "/v1/models/{modelId}";
  private static final String MODEL_PATH = "/v1/manufacturers/{manufacturer}/models/{name}/{year}";
  private static final int YEAR = 2020;
  private static final String MODEL_NAME = "A7";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String MODEL_ID = "52096834-48af-41d1-b422-93600eff629a";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void updateModel_shouldReturnStatus200_whenModelIsInDb() throws Exception {
    ModelDto modelDto = TestDataGenerator.generateModelDto();
    String requestBody = objectMapper.writeValueAsString(modelDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(MODEL_PATH, MANUFACTURER_NAME, MODEL_NAME, YEAR)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk());
  }

  @Test
  void deleteModelById_shouldReturn204_whenModelIsInDb() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(MODEL_ID_PATH, MODEL_ID))
        .andExpect(status().isNoContent());
  }

  //  @BeforeEach
  //  void SetUp() {
  //    modelDto =
  //
  // ModelDto.builder().id(MODEL_ID).year(MODEL_YEAR).categories(Set.of(CATEGORY_NAME)).build();
  //  }
  //
  //  @Test
  //  void searchByManufacturerAndModelAndYear_ShouldReturnStaus200() throws Exception {
  //    mockMvc
  //        .perform(
  //            get(
  //                "/v1/manufacturers/{manufacturer}/models/{model}/{year}",
  //                MANUFACTURER_NAME,
  //                MODEL_NAME,
  //                MODEL_YEAR))
  //        .andExpect(status().isOk());
  //  }
  //
  //  @Test
  //  void search_ShouldReturnStatus200_WhenParametersArePresent() throws Exception {
  //    mockMvc
  //        .perform(
  //            get("/v1/models")
  //                .param("model", MODEL_NAME)
  //                .param("category", CATEGORY_NAME)
  //                .param("manufacturer", MANUFACTURER_NAME)
  //                .param("maxYear", String.valueOf(MODEL_YEAR))
  //                .param("minYear", String.valueOf(MODEL_YEAR)))
  //        .andExpect(status().isOk())
  //        .andExpect(jsonPath("$.content", hasSize(1)))
  //        .andExpect(jsonPath("$.content[0].year", is(MODEL_YEAR)))
  //        .andExpect(jsonPath("$.content[0].manufacturer", is(MANUFACTURER_NAME)))
  //        .andExpect(jsonPath("$.content[0].categories[0]", is(CATEGORY_NAME)));
  //  }
  //
  //  @Test
  //  void search_ShouldReturnStatus200_WhenNoParameters() throws Exception {
  //    mockMvc
  //        .perform(get("/v1/models"))
  //        .andExpect(status().isOk())
  //        .andExpect(jsonPath("$.content", hasSize(1)));
  //  }
  //
  //  @Test
  //  void getById_ShouldReturnStatus200() throws Exception {
  //    mockMvc
  //        .perform(get("/v1/models/{id}", MODEL_ID))
  //        .andExpect(status().isOk())
  //        .andExpect(jsonPath("$.id").value(MODEL_ID));
  //  }
  //
  //  @Test
  //  void create_ShouldReturnStatus201() throws Exception {
  //    String vehicleDtoJson = mapper.writeValueAsString(modelDto);
  //
  //    mockMvc
  //        .perform(
  //            post(
  //                    "/v1/manufacturers/{manufacturer}/models/{name}/{year}",
  //                    MANUFACTURER_NAME,
  //                    MODEL_NAME,
  //                    NEW_MODEL_YEAR)
  //                .contentType(APPLICATION_JSON)
  //                .content(vehicleDtoJson))
  //        .andExpect(status().isCreated())
  //        .andExpect(header().string("Location", containsString("/v1/models/")));
  //  }
  //
  //  @Test
  //  void update_ShouldReturnStatus200() throws Exception {
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
  //        .andExpect(status().isOk());
  //  }
  //
  //  @Test
  //  void deleteById_ShouldReturnStatus204() throws Exception {
  //    mockMvc.perform(delete("/v1/models/{id}", MODEL_ID)).andExpect(status().isNoContent());
  //  }
}
