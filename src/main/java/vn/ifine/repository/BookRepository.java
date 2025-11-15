package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Book;
import vn.ifine.util.BookStatus;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  List<Book> findByCreatedBy(String createdBy);

  List<Book> findByIdIn(List<Long> id);

  Optional<Book> findByIdAndStatus(Long bookId, BookStatus status);

  @Query(value = """
        WITH RECURSIVE seq AS (
          SELECT CAST(:fromDate AS DATE) AS day
          UNION ALL
          SELECT day + INTERVAL 1 DAY FROM seq WHERE day + INTERVAL 1 DAY <= CAST(:toDate AS DATE)
        )
        SELECT seq.day AS day,
               COALESCE(cnt.cnt, 0) AS new_books
        FROM seq
        LEFT JOIN (
          SELECT DATE(created_at) AS day, COUNT(*) AS cnt
          FROM books
          WHERE created_at >= CAST(:fromDate AS DATE)
            AND created_at < CAST(:toDate AS DATE) + INTERVAL 1 DAY
          GROUP BY DATE(created_at)
        ) cnt ON cnt.day = seq.day
        ORDER BY seq.day
        """, nativeQuery = true)
  List<Object[]> findDailyNewBooks(@Param("fromDate") String fromDate, @Param("toDate") String toDate);
}
