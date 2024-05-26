package ua.foxminded.cars.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Sql("/db/manufacturer-test-data.sql")
@TestPropertySource("/application-it.properties")
class ManufacturerControllerIntegrationTest {

  private static final String V1 = "/v1";
  private static final String MANUFACTURER_PATH = "/manufacturers/{name}";
  private static final String MANUFACTURER_NAME = "Audi";

  @Autowired private MockMvc mockMvc;

  @Test
  void getModel_shouldReturnStatus200AndBody_whenManufacturerIsInDb() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(V1 + MANUFACTURER_PATH, MANUFACTURER_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(MANUFACTURER_NAME)));
  }
}