package vn.ifine.specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import vn.ifine.model.User;
import vn.ifine.util.BookStatus;
import vn.ifine.util.UserStatus;

public class UserSpecification {

  public static Specification<User> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.equal(root.get("status"), UserStatus.ACTIVE));

      if (keyword != null && !keyword.trim().isEmpty()) {
        String likePattern = "%" + keyword.toLowerCase() + "%";

        Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern);
        Predicate authorPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern);

        // Gộp các field tìm kiếm bằng OR
        Predicate searchPredicate = criteriaBuilder.or(namePredicate, authorPredicate);

        // Thêm điều kiện search vào danh sách AND
        predicates.add(searchPredicate);
      }

      // Trả về tất cả điều kiện AND
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
