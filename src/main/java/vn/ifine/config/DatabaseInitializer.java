package vn.ifine.config;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.ifine.model.Permission;
import vn.ifine.model.Role;
import vn.ifine.model.User;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.repository.RoleRepository;
import vn.ifine.repository.UserRepository;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Service
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) throws Exception {
    System.out.println(">>> START INIT DATABASE");
    long countUsers = this.userRepository.count();
    long countPermissions = this.permissionRepository.count();
    long countRoles = this.roleRepository.count();

    if (countPermissions == 0) {
      ArrayList<Permission> arr = new ArrayList<>();

      arr.add(new Permission("Create a permission", "/permission/", "POST", "PERMISSIONS"));
      arr.add(new Permission("Update a permission", "/permission/{id}", "PUT", "PERMISSIONS"));
      arr.add(new Permission("Delete a permission", "/permission/{id}", "DELETE", "PERMISSIONS"));
      arr.add(new Permission("Get a permission by id", "/permission/{id}", "GET", "PERMISSIONS"));
      arr.add(new Permission("Get permissions with pagination", "/permission/list", "GET",
          "PERMISSIONS"));

      arr.add(new Permission("Create a role", "/role/", "POST", "ROLES"));
      arr.add(new Permission("Update a role", "/role/{id}", "PUT", "ROLES"));
      arr.add(new Permission("Delete a role", "/role/{id}", "DELETE", "ROLES"));
      arr.add(new Permission("Get a role by id", "/role/{id}", "GET", "ROLES"));
      arr.add(new Permission("Get roles with pagination", "/role/list", "GET", "ROLES"));

      arr.add(new Permission("Get a category by id", "/category/{id}", "GET", "CATEGORIES"));
      arr.add(new Permission("Update a category", "/category/{id}", "PUT", "CATEGORIES"));
      arr.add(new Permission("Delete a category", "/category/{id}", "DELETE", "CATEGORIES"));
      arr.add(new Permission("Create a category", "/category/", "POST", "CATEGORIES"));
      arr.add(
          new Permission("Get categories with pagination", "/category/list", "GET", "CATEGORIES"));
      arr.add(
          new Permission("Get categories upload with pagination", "/category/list-upload", "GET",
              "CATEGORIES"));

      arr.add(new Permission("Create a follow", "/follow/", "POST", "FOLLOWS"));
      arr.add(new Permission("Unfollow", "/follow/", "DELETE", "FOLLOWS"));
      arr.add(new Permission("Get follows with pagination", "/follow/list", "GET", "FOLLOWS"));
      arr.add(new Permission("Get followings with pagination", "/follow/list-following", "GET",
          "FOLLOWS"));
      arr.add(new Permission("Delete a follow", "/follow/{id}", "DELETE", "FOLLOWS"));

      arr.add(new Permission("Add book to favorite", "/favorite-book/", "POST", "FAVORITES"));
      arr.add(
          new Permission("Delete book from favorite", "/favorite-book/", "DELETE", "FAVORITES"));
      arr.add(new Permission("Get favorites of user with pagination", "/favorite-book/list-of-user",
          "GET", "FAVORITES"));
      arr.add(new Permission("Get favorite book list of user with pagination",
          "/favorite-book/books-of-user/{userId}", "GET", "FAVORITES"));

      arr.add(new Permission("Create a comment", "/review/comment", "POST", "COMMENTS"));
      arr.add(new Permission("Update a comment", "/review/comment/{id}", "PUT", "COMMENTS"));
      arr.add(new Permission("Delete a comment", "/review/comment/{id}", "DELETE", "COMMENTS"));
      arr.add(new Permission("Get comments with pagination", "/review/comment/list", "GET",
          "COMMENTS"));

      arr.add(new Permission("Create a rating", "/review/rating", "POST", "RATINGS"));
      arr.add(new Permission("Update a rating", "/review/rating/{id}", "PUT", "RATINGS"));
      arr.add(new Permission("Delete a rating", "/review/rating/{id}", "DELETE", "RATINGS"));
      arr.add(
          new Permission("Get ratings with pagination", "/review/rating/list", "GET", "RATINGS"));

      arr.add(new Permission("Create a review", "/review/{bookId}", "POST", "REVIEWS"));
      arr.add(new Permission("Delete a review", "/review/", "DELETE", "REVIEWS"));
      arr.add(new Permission("Update a review", "/review/update-review", "PUT", "REVIEWS"));

      arr.add(new Permission("Get a user by id", "/user/{id}", "GET", "USERS"));
      arr.add(new Permission("Update a user", "/user/{id}", "PUT", "USERS"));
      arr.add(new Permission("Delete a user", "/user/{id}", "DELETE", "USERS"));
      arr.add(new Permission("Change user info", "/user/change-info", "PUT", "USERS"));
      arr.add(new Permission("Create a user", "/user/", "POST", "USERS"));
      arr.add(new Permission("Change user password", "/user/change-password", "PATCH", "USERS"));
      arr.add(new Permission("Search user", "/user/search", "GET", "USERS"));
      arr.add(new Permission("Get profile a user", "/user/profile/{id}", "GET", "USERS"));
      arr.add(new Permission("Get users with pagination", "/user/list", "GET", "USERS"));

      arr.add(new Permission("Get a book by id", "/book/{id}", "GET", "BOOKS"));
      arr.add(new Permission("Update a book", "/book/{id}", "PUT", "BOOKS"));
      arr.add(new Permission("Delete a book", "/book/{id}", "DELETE", "BOOKS"));
      arr.add(new Permission("Upload a book", "/book/upload-post", "POST", "BOOKS"));
      arr.add(new Permission("Create a book", "/book/", "POST", "BOOKS"));
      arr.add(new Permission("Reject a book", "/book/reject/{id}", "PATCH", "BOOKS"));
      arr.add(new Permission("Approve a book", "/book/approve/{id}", "PATCH", "BOOKS"));
      arr.add(new Permission("Search book", "/book/search", "GET", "BOOKS"));
      arr.add(new Permission("Get books with pagination", "/book/list", "GET", "BOOKS"));
      arr.add(new Permission("Get books approval", "/book/list-none", "GET", "BOOKS"));
      arr.add(new Permission("Get books of user", "/book/list-book-user", "GET", "BOOKS"));
      arr.add(new Permission("Get home book", "/book/home-page", "GET", "BOOKS"));
      arr.add(new Permission("Get explore", "/book/explore", "GET", "BOOKS"));
      arr.add(new Permission("Get a book detail", "/book/detail-book/{id}", "GET", "BOOKS"));

      arr.add(new Permission("Get statistics of dashboard", "/dashboard", "GET", "DASHBOARD"));

      permissionRepository.saveAll(arr);
    }

    if (countRoles == 0) {
      List<Permission> allPermissions = this.permissionRepository.findAll();

      Role adminRole = new Role();
      adminRole.setName("SUPER_ADMIN");
      adminRole.setDescription("Admin has full permissions");
      adminRole.setPermissions(allPermissions);

      this.roleRepository.save(adminRole);

      List<Permission> userPermissions = new ArrayList<>();
      userPermissions.add(permissionRepository.findByApiPathAndMethod("/follow/", "POST").get());
      userPermissions.add(permissionRepository.findByApiPathAndMethod("/follow/", "DELETE").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/follow/list-following", "GET").get());
      userPermissions.add(permissionRepository.findByApiPathAndMethod("/user/{id}", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/user/change-info", "PUT").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/user/change-password", "PATCH").get());
      userPermissions.add(permissionRepository.findByApiPathAndMethod("/user/search", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/user/profile/{id}", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/book/detail-book/{id}", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/book/upload-post", "POST").get());
      userPermissions.add(permissionRepository.findByApiPathAndMethod("/book/search", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/book/list-book-user", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/book/home-page", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/book/explore", "GET").get());
      userPermissions.add(
          permissionRepository.findByApiPathAndMethod("/category/list-upload", "GET").get());
      userPermissions.addAll(permissionRepository.findByModule("FAVORITES"));
      userPermissions.addAll(permissionRepository.findByModule("REVIEWS"));

      Role userRole = new Role();
      userRole.setName("USER");
      userRole.setDescription("Vai trò của user");
      userRole.setPermissions(userPermissions);

      this.roleRepository.save(userRole);
    }

    if (countUsers == 0) {
      User adminUser = new User();
      adminUser.setEmail("admin@gmail.com");
      adminUser.setAddress("Hà Nội");
      adminUser.setGender(GenderEnum.MALE);
      adminUser.setFullName("I'm super admin");
      adminUser.setStatus(UserStatus.ACTIVE);
      adminUser.setPassword(this.passwordEncoder.encode("123456"));

      Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
      if (adminRole != null) {
        adminUser.setRole(adminRole);
      }
      this.userRepository.save(adminUser);
    }
    if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
      System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
    } else {
      System.out.println(">>> END INIT DATABASE");
    }
  }
}
