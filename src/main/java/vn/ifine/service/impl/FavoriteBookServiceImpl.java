package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResBookSearch;
import vn.ifine.dto.response.ResFavoriteBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.FavoriteBook;
import vn.ifine.model.User;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.FavoriteBookRepository;
import vn.ifine.service.BookService;
import vn.ifine.service.FavoriteBookService;
import vn.ifine.service.UserService;
import vn.ifine.specification.UserSpecification;

@Service
@Slf4j(topic = "FAVORITE-BOOK-SERVICE-IMPL")
@RequiredArgsConstructor
public class FavoriteBookServiceImpl implements FavoriteBookService {



  private final FavoriteBookRepository favoriteBookRepository;
  private final BookRepository bookRepository;
  private final UserService userService;
  private final BookService bookService;

  @Override
  public ResFavoriteBook createFavoriteBook(Long bookId, String email) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Not found book with bookId = " + bookId));
    User user = userService.getUserByEmail(email);

    FavoriteBook favoriteBook = FavoriteBook.builder()
        .book(book)
        .user(user)
        .build();
    favoriteBook = favoriteBookRepository.save(favoriteBook);
    ResFavoriteBook res = this.convertToRes(favoriteBook);
    return res;
  }

  @Override
  public void deleteFavoriteBook(Long bookId, String email) {
    User user = userService.getUserByEmail(email);
    FavoriteBook favoriteBook = favoriteBookRepository.findByBookIdAndUserId(bookId, user.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found favoriteBook with bookId=" + bookId + " and userId=" + user.getId()));
    favoriteBookRepository.delete(favoriteBook);
  }

  @Override
  public ResultPaginationDTO getBookFavoriteOfUser(Long userId, Specification<FavoriteBook> spec,
      Pageable pageable) {
    User user = userService.getById(userId);

    // Tạo specification để lọc theo userId
    Specification<FavoriteBook> userSpec = (root, query, cb) -> cb.equal(root.get("user").get("id"), user.getId());

    // Kết hợp với specification được truyền vào
    Specification<FavoriteBook> combinedSpec = userSpec.and(spec);

    Page<FavoriteBook> page = favoriteBookRepository.findAll(combinedSpec, pageable);
    List<ResBookSearch> content = page.getContent().stream()
        .map(x -> {
          Book book = x.getBook();
          return bookService.convertToResBookSearch(book);
        })
        .toList();

    ResultPaginationDTO rs = new ResultPaginationDTO();
    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(page.getTotalPages());
    rs.setTotalElements(page.getTotalElements());
    rs.setResult(content);
    return rs;
  }

  private ResFavoriteBook convertToRes(FavoriteBook favoriteBook) {
    ResFavoriteBook res = ResFavoriteBook.builder()
        .id(favoriteBook.getId())
        .userId(favoriteBook.getUser().getId())
        .bookId(favoriteBook.getBook().getId())
        .createdAt(favoriteBook.getCreatedAt())
        .createdBy(favoriteBook.getCreatedBy())
        .build();
    return res;
  }

  @Override
  public ResultPaginationDTO getListFavoriteOfUser(String email, Specification<FavoriteBook> spec,
      Pageable pageable) {
    // Lấy thông tin user từ email
    User user = userService.getUserByEmail(email);

    // Tạo specification để lọc theo userId
    Specification<FavoriteBook> userSpec = (root, query, cb) -> cb.equal(root.get("user").get("id"), user.getId());

    // Kết hợp với specification được truyền vào
    Specification<FavoriteBook> combinedSpec = userSpec.and(spec);

    Page<FavoriteBook> page = favoriteBookRepository.findAll(combinedSpec, pageable);
    List<ResFavoriteBook> content = page.getContent().stream()
        .map(this::convertToRes)
        .toList();

    ResultPaginationDTO rs = new ResultPaginationDTO();
    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(page.getTotalPages());
    rs.setTotalElements(page.getTotalElements());
    rs.setResult(content);
    return rs;
  }
}
