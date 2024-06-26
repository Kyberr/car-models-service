package ua.foxminded.cars.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Sort.Direction;

@ConfigurationProperties
@Data
public class PageSortConfig {

  private String categorySortBy;
  private Direction categorySortDirection;
  private String modelSortBy;
  private Direction modelSortDirection;
  private String manufacturerSortBy;
  private Direction manufacturerSortDirection;
}
