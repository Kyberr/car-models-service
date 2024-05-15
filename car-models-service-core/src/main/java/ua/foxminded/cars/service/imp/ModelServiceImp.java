package ua.foxminded.cars.service.imp;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.cars.config.AppConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelNotFoundException;
import ua.foxminded.cars.exceptionhandler.exceptions.PeriodNotValidException;
import ua.foxminded.cars.mapper.ModelMapper;
import ua.foxminded.cars.repository.ModelRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.repository.specification.ModelSpecification;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.ModelService;
import ua.foxminded.cars.service.ModelYearService;
import ua.foxminded.cars.service.dto.ModelDto;

@Service
@RequiredArgsConstructor
public class ModelServiceImp implements ModelService {

  private static final String SEARCH_MODELS_CACHE = "searchModels";
  private static final String GET_MODEL_BY_ID_CACHE = "getModelById";
  private static final String GET_MODEL_CACHE = "getModel";

  private final ModelRepository modelRepository;
  private final ModelMapper modelMapper;
  private final AppConfig appConfig;
  private final ManufacturerService manufacturerService;
  private final ModelYearService modelYearService;
  private final CategoryService categoryService;

  /**
   * Updates a model, if related entities after the updating have no relations it removes them.
   *
   * @param targetModelDto - the state of a car model that should be in a database
   * @return - a car model object that reflects a database state after updating
   */
  @Override
  @Transactional
  @Caching(
      evict = @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
      put = {
        @CachePut(
            value = GET_MODEL_CACHE,
            key =
                "{ 'getModel', #targetModelDto.manufacturer, #targetModelDto.name, #targetModelDto.year }"),
        @CachePut(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #targetModelDto.id }")
      })
  public ModelDto updateModel(ModelDto targetModelDto) {
    Model sourceModel =
        findModelBySpecification(
            targetModelDto.getManufacturer(), targetModelDto.getName(), targetModelDto.getYear());

    createCategoriesIfNecessary(targetModelDto.getCategories());
    Set<String> sourceCategories = getCategoryNames(sourceModel.getCategories());
    Set<String> targetCategories = new HashSet<>(targetModelDto.getCategories());
    removeModelFromCategories(sourceModel.getId(), sourceCategories, targetCategories);
    targetCategories.removeAll(sourceCategories);
    putModelToCategories(sourceModel.getId(), targetCategories);
    targetModelDto.setId(sourceModel.getId());
    return targetModelDto;
  }

  private Set<String> getCategoryNames(Set<Category> categories) {
    return categories.stream().map(Category::getName).collect(Collectors.toSet());
  }

  private void removeModelFromCategories(
      UUID modelId, Set<String> sourceCategories, Set<String> targetCategories) {
    for (String sourceCategory : sourceCategories) {
      if (!targetCategories.contains(sourceCategory)) {
        modelRepository.removeModelFromCategory(modelId, sourceCategory);
        deleteCategoryIfNecessary(sourceCategory);
      }
    }
  }

  /**
   * Deletes a car model if related entities after deleting the model have no relations it removes
   * them too.
   *
   * @param modelId - ID of a model
   */
  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
        @CacheEvict(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #modelId }"),
        @CacheEvict(value = GET_MODEL_CACHE, allEntries = true)
      })
  public void deleteModelById(UUID modelId) {
    Model model =
        modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    modelRepository.deleteById(modelId);
    deleteManufacturerIfNecessary(model.getManufacturer());
    deleteModelYearIfNecessary(model.getYear());
    deleteCategoriesIfNecessary(model.getCategories());
  }

  private void deleteManufacturerIfNecessary(Manufacturer manufacturer) {
    if (!modelRepository.existsByManufacturerName(manufacturer.getName())) {
      manufacturerService.deleteManufacturer(manufacturer.getName());
    }
  }

  private void deleteModelYearIfNecessary(ModelYear modelYear) {
    if (!modelRepository.existsByYearValue(modelYear.getValue())) {
      modelYearService.deleteYear(modelYear.getValue());
    }
  }

  private void deleteCategoriesIfNecessary(Set<Category> categories) {
    List<String> categoryNames = categories.stream().map(Category::getName).toList();

    for (String categoryName : categoryNames) {
      deleteCategoryIfNecessary(categoryName);
    }
  }

  private void deleteCategoryIfNecessary(String categoryName) {
    if (!modelRepository.existsByCategoriesName(categoryName)) {
      categoryService.deleteCategory(categoryName);
    }
  }

  @Override
  @Cacheable(value = GET_MODEL_BY_ID_CACHE, key = "{ #root.methodName, #modelId }")
  public ModelDto getModelById(UUID modelId) {
    Model model =
        modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    return modelMapper.toDto(model);
  }

  @Override
  @Cacheable(value = GET_MODEL_CACHE, key = "{ #root.methodName,  #manufacturer, #name, #year }")
  public ModelDto getModel(String manufacturer, String name, int year) {
    Model model = findModelBySpecification(manufacturer, name, year);
    return modelMapper.toDto(model);
  }

  private Model findModelBySpecification(String manufacturer, String modelName, int modelYear) {
    Specification<Model> specification = buildSpecification(manufacturer, modelName, modelYear);
    return modelRepository
        .findOne(specification)
        .orElseThrow(() -> new ModelNotFoundException(manufacturer, modelName, modelYear));
  }

  /**
   * Searches for models by provided parameters if no parameters are present it returns all models
   * in a database. There is a validation, maxYear value must be greater than minYear value.
   *
   * @param searchFilter - parameters for the search
   * @param pageable - parameter for a page
   * @return Page<ModelDto> - a page containing models
   */
  @Override
  @Cacheable(value = SEARCH_MODELS_CACHE, key = "{ #root.methodName, #searchFilter, #pageable }")
  public Page<ModelDto> searchModel(SearchFilter searchFilter, Pageable pageable) {
    SearchFilter validatedSearchFilter = validateSearchFilter(searchFilter);
    Specification<Model> specification = ModelSpecification.getSpecification(validatedSearchFilter);
    pageable = setDefaultSortIfNecessary(pageable);
    return modelRepository.findAll(specification, pageable).map(modelMapper::toDto);
  }

  private SearchFilter validateSearchFilter(SearchFilter searchFilter) {
    Integer minYear = searchFilter.getMinYear();
    Integer maxYear = searchFilter.getMaxYear();

    if (Objects.nonNull(minYear) && Objects.nonNull(maxYear) && minYear > maxYear) {
      throw new PeriodNotValidException(minYear, maxYear);
    } else {
      return searchFilter;
    }
  }

  private Pageable setDefaultSortIfNecessary(Pageable pageRequest) {
    if (pageRequest.getSort().isUnsorted()) {
      Sort defaulSort = Sort.by(appConfig.getModelSortDirection(), appConfig.getModelSortBy());
      return PageRequest.of(
          pageRequest.getPageNumber(),
          pageRequest.getPageSize(),
          pageRequest.getSortOr(defaulSort));
    }
    return pageRequest;
  }

  /**
   * Creates a model. If a database has no necessary entities it creates them.
   *
   * @param modelDto - DTO of a model
   * @return ModelDto
   */
  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
        @CacheEvict(value = GET_MODEL_BY_ID_CACHE, allEntries = true)
      },
      put = {
        @CachePut(
            value = GET_MODEL_CACHE,
            key = "{ 'getModel', #modelDto.manufacturer, #modelDto.name, #modelDto.year }")
      })
  public ModelDto createModel(ModelDto modelDto) {
    verifyIfModelExists(modelDto.getManufacturer(), modelDto.getName(), modelDto.getYear());
    manufacturerService.createManufacturerIfNecessary(modelDto.getManufacturer());
    Year year = Year.of(modelDto.getYear());
    modelYearService.createYearIfNecessary(year);
    Model model = modelMapper.toEntity(modelDto);
    Model savedModel = modelRepository.save(model);
    createCategoriesIfNecessary(modelDto.getCategories());
    putModelToCategories(savedModel.getId(), modelDto.getCategories());
    modelDto.setId(savedModel.getId());
    return modelDto;
  }

  private void verifyIfModelExists(String manufacturerName, String modelName, int year) {
    Specification<Model> specification = buildSpecification(manufacturerName, modelName, year);
    modelRepository
        .findOne(specification)
        .ifPresent(
            entity -> {
              throw new ModelAlreadyExistsException(
                  manufacturerName, modelName, year, entity.getId());
            });
  }

  private Specification<Model> buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(manufacturer).name(name).year(year).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  private void createCategoriesIfNecessary(Set<String> categoryNames) {
    for (String name : categoryNames) {
      categoryService.createCategoryIfNecessary(name);
    }
  }

  private void putModelToCategories(UUID modelId, Set<String> categoryNames) {
    for (String categoryName : categoryNames) {
      modelRepository.putModelToCategory(modelId, categoryName);
    }
  }
}
