package vn.ifine.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.ifine.model.User;
import vn.ifine.util.UserStatus;

public class UserSpecification {
  public static Specification<User> isActive() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), UserStatus.ACTIVE);
  }

  // Kết hợp nhiều điều kiện
  public static Specification<User> withFilter(Specification<User> spec) {
    return Specification.where(isActive()).and(spec);
  }
}
