package ua.foxminded.cars.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import ua.foxminded.cars.config.TestConfig;
import ua.foxminded.cars.testcontainer.KeycloakTestcontainer;

@Slf4j
@SpringBootTest(classes = TestConfig.class)
public abstract class ComponentTestContext {

  private static final String AUTHORIZATION_SERVER_ALIAS = "keycloak";
  private static final String DATABASE_ALIAS = "postgres";
  private static final String CLIENT_SECRET = "secret";
  private static final String ADMIN_USER = "admin";
  private static final String ADMIN_PASSWORD = "admin";

  private static Network network = Network.newNetwork();
  private static KeycloakTestcontainer keycloak;
  private static PostgreSQLContainer<?> postgres;
  private static GenericContainer<?> carsModelsService;

  static {
    keycloak =
        new KeycloakTestcontainer()
            .withRealmImportFile("/realm-config.json")
            .withNetworkAliases(AUTHORIZATION_SERVER_ALIAS)
            .withContextPath("/auth")
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(log));
    keycloak.start();

    postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("cars")
            .withUsername("cars")
            .withPassword("cars")
            .withNetwork(network)
            .withNetworkAliases(DATABASE_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(log));
    postgres.start();

    carsModelsService =
        new GenericContainer<>("foxminded/car-models-service:latest")
            .withExposedPorts(8180)
            .withNetwork(network)
            .withEnv("KEYCLOAK_HOST", AUTHORIZATION_SERVER_ALIAS)
            .withEnv("POSTGRES_HOST", DATABASE_ALIAS)
            .dependsOn(postgres)
            .dependsOn(keycloak)
            .withLogConsumer(new Slf4jLogConsumer(log));
    carsModelsService.start();
  }

  public WebTestClient webTestClient;
  public String carModelServiceBaseUrl;

  @Autowired private PolicyEnforcerConfig policyEnforcerConfig;

  @BeforeEach
  void setUp() {
    carModelServiceBaseUrl =
        "http://" + carsModelsService.getHost() + ":" + carsModelsService.getMappedPort(8180);
    webTestClient = WebTestClient.bindToServer().baseUrl(carModelServiceBaseUrl).build();
    JdbcDatabaseDelegate databaseDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(databaseDelegate, "test_data.sql");
  }

  public String getAdminRoleBearerToken() {
    return getBearerToken(ADMIN_USER, ADMIN_PASSWORD);
  }

  private String getBearerToken(String username, String password) {
    Keycloak keycloakInstance =
        Keycloak.getInstance(
            keycloak.getAuthServerUrl(),
            policyEnforcerConfig.getRealm(),
            username,
            password,
            policyEnforcerConfig.getResource(),
            policyEnforcerConfig.getCredentials().get(CLIENT_SECRET).toString());

    String accessToken = keycloakInstance.tokenManager().getAccessTokenString();
    return "Bearer " + accessToken;
  }
}
