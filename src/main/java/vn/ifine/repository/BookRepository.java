package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Book;
import vn.ifine.util.BookStatus;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  List<Book> findByCreatedBy(String createdBy);

  List<Book> findByIdIn(List<Long> id);

  Optional<Book> findByIdAndStatus(Long bookId, BookStatus status);
}
