package vn.ifine.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>,
    JpaSpecificationExecutor<Category> {

  boolean existsByName(String name);

  Set<Category> findByIdIn(Set<Integer> id);
}
