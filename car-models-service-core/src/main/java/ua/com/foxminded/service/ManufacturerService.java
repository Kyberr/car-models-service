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
package ua.com.foxminded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.dto.ManufacturerDto;
import ua.com.foxminded.entity.Manufacturer;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.ManufacturerMapper;
import ua.com.foxminded.repository.ManufacturerRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerService {

  public static final String NO_MANUFACTURER = "The manufacturer '%s' doesn't exist";
  public static final String MANUFACTURER_ALREADY_EXISTS = "The manufacturer '%s' already exists";

  private final ManufacturerRepository manufacturerRepository;
  private final ManufacturerMapper manufacturerMapper;

  @CacheEvict(value = {"ManufacturerService.getAll"}, allEntries = true)
  @Caching(evict = {
      @CacheEvict(value = "ManufacturerService.getAll", allEntries = true),
      @CacheEvict(value = "ManufacturerService.getByName", key = "#manufacturerDto.name")
  })
  public ManufacturerDto create(ManufacturerDto manufacturerDto) {
    verifyIfManufacturerExists(manufacturerDto.getName());
    Manufacturer manufacturer = manufacturerMapper.map(manufacturerDto);
    Manufacturer persistedManufacturer = manufacturerRepository.save(manufacturer);
    return manufacturerMapper.map(persistedManufacturer);
  }

  private void verifyIfManufacturerExists(String name) {
    if (manufacturerRepository.existsById(name)) {
      throw new AlreadyExistsException(
          String.format(MANUFACTURER_ALREADY_EXISTS, name));
    }
  }

  @Cacheable("ManufacturerService.getAll")
  public Page<ManufacturerDto> getAll(Pageable pageable) {
    return manufacturerRepository.findAll(pageable).map(manufacturerMapper::map);
  }

  @CacheEvict(value = {"ManufacturerService.getAll"}, allEntries = true)
  @Caching(evict = {
      @CacheEvict(value = "ManufacturerService.getAll", allEntries = true),
      @CacheEvict(value = "ManufacturerService.getByName", key = "#name")
  })
  public void deleteByName(String name) {
    manufacturerRepository
        .findById(name)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MANUFACTURER, name)));
    manufacturerRepository.deleteById(name);
  }

  @Cacheable(value = "ManufacturerService.getByName")
  public ManufacturerDto getByName(String name) {
    return manufacturerRepository
        .findById(name)
        .map(manufacturerMapper::map)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MANUFACTURER, name)));
  }
}
