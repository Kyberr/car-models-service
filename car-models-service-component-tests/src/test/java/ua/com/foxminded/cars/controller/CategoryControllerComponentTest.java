package ua.com.foxminded.cars.controller;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import ua.com.foxminded.cars.dto.CategoryDto;


class CategoryControllerComponentTest extends ComponentTestContext {
    
    public static final String CATEGORY_WITHOUT_RELATIONS = "Coupe";
    public static final String CATEGORY = "Sedan";
    public static final String NOT_EXISTING_CATEGORY = "Pickup";
    
    @Test
    void save_ShouldReturnStatus409_WhenSuchCategoryAlreadyExists() {
        CategoryDto categoryDto = CategoryDto.builder().name(CATEGORY).build();
        
        webTestClient.post().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(categoryDto)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
    
    @Test
    void save_ShouldReturnStatus201() {
        CategoryDto categoryDto = CategoryDto.builder().name(NOT_EXISTING_CATEGORY).build();
        
        webTestClient.post().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .bodyValue(categoryDto)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().location(carModelServiceBaseUrl + "/v1/categories/" + NOT_EXISTING_CATEGORY);
    }
    
    @Test
    void getByName_ShoudReturnStatus200_WhenNoSuchCategory() {
        webTestClient.get().uri("/v1/categories/{category}", NOT_EXISTING_CATEGORY)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk();
    }
    
    @Test
    void getByName_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/categories/{category}", CATEGORY)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.name").isEqualTo(CATEGORY);
    }
    
    @Test
    void getAll_ShouldReturnStatus200() {
        webTestClient.get().uri("/v1/categories")
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody().jsonPath("$.content", hasSize(2));
    }
    
    @Test
    void deleleteByName_ShouldReturnStatus404_WhenNoSuchCategory() {
        webTestClient.delete().uri("/v1/categories/{category}", NOT_EXISTING_CATEGORY)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNotFound();
    }
    

    @Test
    void deleteByName_ShouldReturnStatus200() {
        webTestClient.delete().uri("/v1/categories/{category}", CATEGORY)
                     .header(AUTHORIZATION_HEADER, getAdminRoleBearerToken())
                     .exchange()
                     .expectStatus().isNoContent()
                     .expectBody().isEmpty();
    }
}
