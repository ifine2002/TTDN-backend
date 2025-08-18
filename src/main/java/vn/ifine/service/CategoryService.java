package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCategoryDTO;
import vn.ifine.dto.response.ResCategory;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Category;

@Service
public interface CategoryService {

  ResCategory create(ReqCategoryDTO reqCategory);

  ResCategory update(int categoryId, ReqCategoryDTO reqCategory);

  void remove(int categoryId);

  ResultPaginationDTO getCategories(Specification<Category> spec, Pageable pageable);

  Category getById(int id);

  boolean isNameExist(String name);

  ResCategory convertToResCategory(Category category);

  ResultPaginationDTO getCategoriesUpload(Specification<Category> spec, Pageable pageable);
}
