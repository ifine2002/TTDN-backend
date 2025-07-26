package vn.ifine.specification;

import org.springframework.data.jpa.domain.Specification;

public class GenericSpecification<T> {

  // Generic isActive specification
  public static <T> Specification<T> isActive() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("isActive"), true);
  }

  // Method to combine multiple conditions
  public static <T> Specification<T> withFilter(Specification<T> spec) {
    return Specification.where(GenericSpecification.<T>isActive()).and(spec);
  }
}

/** check với điều kiện khác
 * public static <T> Specification<T> isActive(String fieldName) { return (root, query, cb) ->
 * cb.equal(root.get(fieldName), true); }
 * <p>
 * public static <T> Specification<T> withFilter(Specification<T> spec, String fieldName) { return
 * Specification.where(GenericSpecification.<T>isActive(fieldName)).and(spec); }
 */