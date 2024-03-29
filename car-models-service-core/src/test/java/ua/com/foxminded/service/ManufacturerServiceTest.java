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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.dto.ManufacturerDto;
import ua.com.foxminded.entity.Manufacturer;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.ManufacturerMapper;
import ua.com.foxminded.mapper.ManufacturerMapperImpl;
import ua.com.foxminded.repository.ManufacturerRepository;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

  public static final String MANUFACTURER_NAME = "Audi";

  @InjectMocks private ManufacturerService manufacturerService;

  @Mock private ManufacturerRepository manufacturerRepository;

  @Spy private ManufacturerMapper manufacturerMapper = new ManufacturerMapperImpl();

  private Manufacturer manufacturer;
  private ManufacturerDto manufacturerDto;

  @BeforeEach
  void SetUp() {
    manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
    manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
  }

  @Test
  void create_ShouldSaveManufacturer() {
    when(manufacturerRepository.existsById(manufacturerDto.getName())).thenReturn(false);
    when(manufacturerMapper.map(manufacturerDto)).thenReturn(manufacturer);
    manufacturerService.create(manufacturerDto);

    verify(manufacturerRepository).existsById(manufacturerDto.getName());
    verify(manufacturerMapper).map(manufacturerDto);
    verify(manufacturerRepository).save(manufacturer);
  }

  @Test
  void create_ShouldThrowAlreadyExistsExcepion_WhenNoSuchManufacturer() {
    when(manufacturerRepository.existsById(manufacturerDto.getName())).thenReturn(true);

    assertThrows(AlreadyExistsException.class, () -> manufacturerService.create(manufacturerDto));
  }

  @Test
  void getAll_ShouldGetManufacturers() {
    List<Manufacturer> manufacturers = Arrays.asList(manufacturer);
    Pageable pageable = Pageable.unpaged();
    Page<Manufacturer> manufacturerPage = new PageImpl<>(manufacturers);
    when(manufacturerRepository.findAll(pageable)).thenReturn(manufacturerPage);
    manufacturerService.getAll(pageable);

    verify(manufacturerRepository).findAll(pageable);
    verify(manufacturerMapper).map(manufacturer);
  }

  @Test
  void deleteByName_ShouldDeleteManufacturer() {
    manufacturer.setModels(new HashSet<>());
    when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));
    manufacturerService.deleteByName(MANUFACTURER_NAME);

    verify(manufacturerRepository).findById(MANUFACTURER_NAME);
    verify(manufacturerRepository).deleteById(MANUFACTURER_NAME);
  }

  @Test
  void deleteByName_ShouldThrowNotFoundException_WhenNoSuchManufacturer() {
    when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> manufacturerService.deleteByName(MANUFACTURER_NAME));
  }

  @Test
  void getByName_ShouldGetManufacturer() {
    when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));
    manufacturerService.getByName(MANUFACTURER_NAME);

    verify(manufacturerRepository).findById(MANUFACTURER_NAME);
    verify(manufacturerMapper).map(manufacturer);
  }
}
