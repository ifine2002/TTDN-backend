package vn.ifine.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ifine.dto.request.ReqCategoryDTO;
import vn.ifine.dto.response.ResCategory;
import vn.ifine.dto.response.ResCategoryUpload;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Category;
import vn.ifine.repository.CategoryRepository;
import vn.ifine.service.CategoryService;
import vn.ifine.service.FileService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE-IMPL")
public class CategoryServiceImpl implements CategoryService {

  @Override
  public boolean isNameExist(String name) {
    return categoryRepository.existsByName(name);
  }

  private final CategoryRepository categoryRepository;
  private final FileService fileService;

  @Override
  @Transactional
  public ResCategory create(ReqCategoryDTO reqCategory) {
    log.info("Request create category, name={}", reqCategory.getName());
    Category category = Category.builder()
        .name(reqCategory.getName())
        .description(reqCategory.getDescription())
        .image(reqCategory.getImage() != null ? fileService.upload(reqCategory.getImage()) : null)
        .build();

    categoryRepository.save(category);
    return this.convertToResCategory(category);
  }

  @Override
  @Transactional
  public ResCategory update(int categoryId, ReqCategoryDTO reqCategory) {
    log.info("Request update category, name={}", reqCategory.getName());
    Category categoryDB = this.getById(categoryId);
    if(categoryDB != null){
      categoryDB.setName(reqCategory.getName());
      categoryDB.setDescription(reqCategory.getDescription());
      if(reqCategory.getImage() != null) {
        categoryDB.setImage(fileService.upload(reqCategory.getImage()));
      }
      if(reqCategory.isDeleteImage()){
        categoryDB.setImage(null);
      }
    }
    categoryRepository.save(categoryDB);
    return this.convertToResCategory(categoryDB);
  }

  @Override
  public void remove(int categoryId) {
    Category category = this.getById(categoryId);
    if(category == null){
      throw new ResourceNotFoundException("Not found category with id = " + categoryId);
    }
    log.info("Request delete category, id={}", categoryId);
    categoryRepository.delete(category);
  }

  @Override
  public ResultPaginationDTO getCategories(Specification<Category> spec, Pageable pageable) {
    Page<Category> pageCategory = categoryRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pageCategory.getTotalPages());
    rs.setTotalElements(pageCategory.getTotalElements());
  // convert data
    List<ResCategory> listCategory = pageCategory.getContent()
        .stream().map(this::convertToResCategory)
        .toList();
    rs.setResult(listCategory);
    return rs;
  }

  @Override
  public ResultPaginationDTO getCategoriesUpload(Specification<Category> spec, Pageable pageable) {
    Page<Category> pageCategory = categoryRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pageCategory.getTotalPages());
    rs.setTotalElements(pageCategory.getTotalElements());
    // convert data
    List<ResCategoryUpload> listCategory = pageCategory.getContent()
        .stream().map(c -> new ResCategoryUpload(c.getId(), c.getName()))
        .toList();
    rs.setResult(listCategory);
    return rs;
  }

  @Override
  public Category getById(int id) {
    log.info("Request get category, id={}", id);
    Optional<Category> categoryOptional = categoryRepository.findById(id);
    if (!categoryOptional.isPresent()){
      throw new ResourceNotFoundException("Not found category with id = " + id);
    }
    return categoryOptional.get();
  }

  @Override
  public ResCategory convertToResCategory(Category category) {
    ResCategory res = ResCategory.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .image(category.getImage())
        .isActive(category.getIsActive())
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .createdBy(category.getCreatedBy())
        .updatedBy(category.getUpdatedBy())
        .build();
    return res;
  }
}
