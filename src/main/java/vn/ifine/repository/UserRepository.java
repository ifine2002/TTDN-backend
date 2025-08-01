package vn.ifine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ifine.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
