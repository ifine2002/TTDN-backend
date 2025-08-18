package vn.ifine.specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import vn.ifine.model.Book;
import vn.ifine.util.BookStatus;

public class BookSpecification {
  public static Specification<Book> isActive() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), BookStatus.ACTIVE);
  }

  public static Specification<Book> isNone() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), BookStatus.NONE);
  }

  // Active
  public static Specification<Book> activeWithFilter(Specification<Book> spec) {
    return Specification.where(isActive()).and(spec);
  }

  // None
  public static Specification<Book> noneWithFilter(Specification<Book> spec) {
    return Specification.where(isNone()).and(spec);
  }

  public static Specification<Book> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.equal(root.get("status"), BookStatus.ACTIVE));

      if (keyword != null && !keyword.trim().isEmpty()) {
        String likePattern = "%" + keyword.toLowerCase() + "%";

        Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
        Predicate authorPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), likePattern);

        // Gộp các field tìm kiếm bằng OR
        Predicate searchPredicate = criteriaBuilder.or(namePredicate, authorPredicate);

        // Thêm điều kiện search vào danh sách AND
        predicates.add(searchPredicate);
      }

      // Trả về tất cả điều kiện AND
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Book> hasCreatedBy(String email) {
    return (root, query, criteriaBuilder) -> {
      if (email == null || email.trim().isEmpty()) {
        return criteriaBuilder.conjunction(); // TRUE
      }
      return criteriaBuilder.equal(root.get("createdBy"), email);
    };
  }

  // Kết hợp cả 2 điều kiện
  public static Specification<Book> activeByCreator(String email) {
    return Specification.where(isActive()).and(hasCreatedBy(email));
  }

}
