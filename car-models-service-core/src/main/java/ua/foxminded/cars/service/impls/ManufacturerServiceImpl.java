package ua.foxminded.cars.service.impls;

import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.MANUFACTURER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.config.PageSortConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.mapper.ManufacturerMapper;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.AbstractService;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManufacturerServiceImpl extends AbstractService implements ManufacturerService {

  private final ManufacturerRepository manufacturerRepository;
  private final ManufacturerMapper manufacturerMapper;
  private final PageSortConfig pageSortConfig;

  @Override
  public Page<ManufacturerDto> getAllManufacturers(Pageable pageable) {
    pageable =
        setDefaultSortIfNecessary(
            pageable,
            pageSortConfig.getManufacturerSortDirection(),
            pageSortConfig.getManufacturerSortBy());
    return manufacturerRepository.findAll(pageable).map(manufacturerMapper::toDto);
  }

  @Override
  public ManufacturerDto getManufacturer(String name) {
    Manufacturer manufacturer =
        manufacturerRepository
            .findById(name)
            .orElseThrow(() -> new ManufacturerNotFoundException(name));
    return manufacturerMapper.toDto(manufacturer);
  }

  @Override
  public void deleteManufacturer(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      log.debug(MANUFACTURER_NOT_FOUND.formatted(manufacturerName));
      throw new ManufacturerNotFoundException(manufacturerName);
    }
    manufacturerRepository.deleteById(manufacturerName);
  }

  @Override
  public boolean isManufacturerExistByName(String name) {
    return manufacturerRepository.existsById(name);
  }

  @Override
  public ManufacturerDto createManufacturer(ManufacturerDto manufacturerDto) {
    Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDto);
    Manufacturer savedManufacturer = manufacturerRepository.saveAndFlush(manufacturer);
    return manufacturerMapper.toDto(savedManufacturer);
  }
}
