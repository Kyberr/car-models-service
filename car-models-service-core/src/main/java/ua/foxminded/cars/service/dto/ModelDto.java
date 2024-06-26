package ua.foxminded.cars.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {

  private UUID id;

  private String name;

  private Integer year;

  private String manufacturer;

  @NotNull private List<@NotBlank String> categories;
}
