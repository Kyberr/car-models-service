package ua.com.foxminded.vehicles.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.service.VehicleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Validated
public class VehicleController {
    
    public static final String PRODUCTION_YEAR_FIELD = "productionYear";
    public static final String MIN_PRODUCTION_YEAR = "minYear";
    public static final String MAX_PRODUCTION_YEAR = "maxYear";
    
    private final VehicleService vehicleService;
    
    @GetMapping(value = "/models/{model}/vehicles")
    public Page<VehicleDto> getByModel(@PathVariable String model, 
                                       @SortDefault(sort = PRODUCTION_YEAR_FIELD, 
                                                    direction = Sort.Direction.DESC)
                                       Pageable pageable) {
        return vehicleService.getByModel(model, pageable);
    }
    
    @GetMapping(value = "/categories/{category}/vehicles")
    public Page<VehicleDto> getByCategory(@PathVariable String category, 
                                          @SortDefault(sort = PRODUCTION_YEAR_FIELD, 
                                                       direction = Sort.Direction.DESC)   
                                          Pageable pageable) {
        return vehicleService.getByCategory(category, pageable);
    }
    
    @GetMapping(value = "/manufacturers/{manufacturer}/vehicles", params = MAX_PRODUCTION_YEAR) 
    public Page<VehicleDto> getByManufacturerAndMaxProductionYear(@NotBlank @PathVariable String manufacturer, 
                                                                  @RequestParam int maxYear, 
                                                                  @SortDefault(sort = PRODUCTION_YEAR_FIELD, 
                                                                               direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMaxYear(manufacturer, maxYear, pageable);
    }
    
    @GetMapping(value = "/manufacturers/{manufacturer}/vehicles", params = MIN_PRODUCTION_YEAR) 
    public Page<VehicleDto> getByManufacturerAndMinProductionYear(@PathVariable String manufacturer,
                                                                  @RequestParam int minYear, 
                                                                  @SortDefault(sort = PRODUCTION_YEAR_FIELD, 
                                                                               direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        
        return vehicleService.getByManufacturerNameAndMinYear(manufacturer, minYear, pageable);
    }
    
    @PostMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> save(@PathVariable String manufacturer, 
                                       @PathVariable String model, 
                                       @PathVariable int year, 
                                       @RequestBody VehicleDto vehicle, 
                                       HttpServletRequest request) {
        vehicle.setManufacturer(ManufacturerDto.builder().name(manufacturer).build());
        vehicle.setModel(ModelDto.builder().name(model).build());
        vehicle.setProductionYear(year);

        VehicleDto createdVehicle = vehicleService.save(vehicle);
        
        URI location = UriComponentsBuilder.newInstance().scheme(request.getScheme())
                                                         .host(request.getServerName())
                                                         .port(request.getServerPort())
                                                         .path("/v1/vehicles/{id}")
                                                         .buildAndExpand(createdVehicle.getId())
                                                         .toUri();
        return ResponseEntity.created(location).build();
    }
    
    @GetMapping("/vehicles")
    public Page<VehicleDto> getAll(@SortDefault(sort = PRODUCTION_YEAR_FIELD, 
                                                direction = Sort.Direction.DESC)
                                   Pageable pageable) {
        return vehicleService.getAll(pageable);
    }
    
    @GetMapping("/vehicles/{id}")
    public VehicleDto getById(@PathVariable String id) {
        return vehicleService.getById(id);
    }
    
    @PutMapping("/manufacturers/{manufacturer}/models/{model}/{year}")
    public ResponseEntity<String> update(@PathVariable String manufacturer, 
                                         @PathVariable String model,
                                         @PathVariable int year,
                                         @RequestBody @Valid VehicleDto vehicle, 
                                         HttpServletRequest request) {
        vehicle.setManufacturer(ManufacturerDto.builder().name(manufacturer).build());
        vehicle.setModel(ModelDto.builder().name(model).build());
        vehicle.setProductionYear(year);
        VehicleDto updatedVehicle = vehicleService.update(vehicle);
        URI location = UriComponentsBuilder.newInstance().scheme(request.getScheme())
                                                         .host(request.getServerName())
                                                         .port(request.getServerPort())
                                                         .path("/v1/vehicles/{id}")
                                                         .buildAndExpand(updatedVehicle.getId())
                                                         .toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }
    
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        vehicleService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
