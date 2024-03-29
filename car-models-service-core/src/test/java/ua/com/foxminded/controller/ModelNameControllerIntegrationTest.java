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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.dto.ModelNameDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ModelNameControllerIntegrationTest {

  public static final String NEW_MODEL_NAME = "Fusion";
  public static final String MODEL_NAME = "A7";
  public static final String MODEL_NAME_WITHOUT_RELATIONS = "A8";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  private ModelNameDto modelNameDto;
  private String modelNameDtoJson;

  @BeforeEach
  void setUp() {
    modelNameDto = ModelNameDto.builder().name(MODEL_NAME).build();
  }

  @Test
  void create_ShouldReturnStatus201() throws Exception {
    modelNameDto.setName(NEW_MODEL_NAME);
    modelNameDtoJson = mapper.writeValueAsString(modelNameDto);

    mockMvc
        .perform(post("/v1/model-names").contentType(APPLICATION_JSON).content(modelNameDtoJson))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", containsString("/v1/model-names/" + NEW_MODEL_NAME)));
  }

  @Test
  void getByName_ShouldReturnStatus200() throws Exception {
    mockMvc
        .perform(get("/v1/model-names/{name}", MODEL_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(MODEL_NAME)));
  }

  @Test
  void getAll_ShouldReturnStatus200() throws Exception {
    mockMvc
        .perform(get("/v1/model-names"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  void deleteByName_ShouldReturnStatus204() throws Exception {
    mockMvc
        .perform(delete("/v1/model-names/{name}", MODEL_NAME_WITHOUT_RELATIONS))
        .andExpect(status().isNoContent());
  }
}
