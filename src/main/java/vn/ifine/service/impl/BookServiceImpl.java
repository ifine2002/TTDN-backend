package vn.ifine.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ResAdminBookDTO;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResBookSearch;
import vn.ifine.dto.response.ResCategoryInBook;
import vn.ifine.dto.response.ResDetailBook;
import vn.ifine.dto.response.ResFeedBack;
import vn.ifine.dto.response.ResPost;
import vn.ifine.dto.response.ResReviewDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.Category;
import vn.ifine.model.Comment;
import vn.ifine.model.Rating;
import vn.ifine.model.User;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.CategoryRepository;
import vn.ifine.repository.CommentRepository;
import vn.ifine.repository.RatingRepository;
import vn.ifine.service.BookService;
import vn.ifine.service.FileService;
import vn.ifine.service.UserService;
import vn.ifine.specification.BookSpecification;
import vn.ifine.util.BookStatus;

@Service
@Slf4j(topic = "BOOK-SERVICE-IMPL")
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {



  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;
  private final RatingRepository ratingRepository;
  private final CommentRepository commentRepository;
  private final FileService fileService;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;

  // for admin
  @Override
  @Transactional
  public ResBook create(ReqBookDTO reqBookDTO) {
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage() != null ? fileService.upload(reqBookDTO.getImage()) : null)
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(reqBookDTO.getStatus())
        .build();

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Admin save book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  // for user
  @Transactional
  @Override
  public ResAdminBookDTO uploadBook(ReqBookDTO reqBookDTO, String email) {
    User user = userService.getUserByEmail(email);
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage() != null ? fileService.upload(reqBookDTO.getImage()) : null)
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(BookStatus.NONE)
        .build();

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);

    ResAdminBookDTO adminBook = this.convertToResAdminBook(book, user);

    this.sendAdminBookNotification("create", adminBook);

    log.info("User upload book success, bookId={}", book.getId());
    return adminBook;
  }

  private ResAdminBookDTO convertToResAdminBook(Book book, User user) {
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());

    return ResAdminBookDTO.builder()
        .bookId(book.getId())
        .bookName(book.getName())
        .description(book.getDescription())
        .publishedDate(book.getPublishedDate())
        .bookFormat(book.getBookFormat())
        .bookSaleLink(book.getBookSaleLink())
        .language(book.getLanguage())
        .imageBook(book.getImage())
        .author(book.getAuthor())
        .categories(resCategories)
        .userId(user.getId())
        .fullName(user.getFullName())
        .avatar(user.getImage())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .build();
  }

  @Override
  public ResultPaginationDTO getApproveBooks(Specification<Book> spec, Pageable pageable) {
    // Kết hợp điều kiện isNone với các điều kiện khác
    Specification<Book> activeSpec = BookSpecification.noneWithFilter(spec);

    Page<Book> pageBook = bookRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResAdminBookDTO> listBook = pageBook.getContent()
        .stream().map(book -> {
          User user = userService.getUserByEmail(book.getCreatedBy());
          return this.convertToResAdminBook(book, user);
        })
        .toList();

    rs.setResult(listBook);
    return rs;
  }

  // admin
  @Transactional
  @Override
  public ResBook update(long bookId, ReqBookDTO reqBookDTO) {
    Book book = this.getById(bookId);
    book.setName(reqBookDTO.getName());
    book.setDescription(reqBookDTO.getDescription());

    if (reqBookDTO.getImage() != null) {
      book.setImage(fileService.upload(reqBookDTO.getImage()));
    }
    if (reqBookDTO.isDeleteImage()) {
      book.setImage(null);
    }

    book.setPublishedDate(reqBookDTO.getPublishedDate());
    book.setBookFormat(reqBookDTO.getBookFormat());
    book.setBookSaleLink(reqBookDTO.getBookSaleLink());
    book.setLanguage(reqBookDTO.getLanguage());
    book.setAuthor(reqBookDTO.getAuthor());
    book.setStatus(reqBookDTO.getStatus());

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {

      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Update book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  private void sendAdminBookNotification(String action, Object data) {
    Map<String, Object> notification = new HashMap<>();
    notification.put("action", action);
    notification.put("data", data);
    notification.put("timestamp", LocalDateTime.now());

    messagingTemplate.convertAndSend("/topic/admin-books", notification);
  }

  @Override
  public void approveBook(Long bookId) {
    Book book = this.getById(bookId);
    book.setStatus(BookStatus.ACTIVE);

    bookRepository.save(book);
    this.sendAdminBookNotification("approve", this.convertToResPost(book));
  }

  @Override
  public void rejectBook(Long bookId) {
    Book book = this.getById(bookId);
    book.setStatus(BookStatus.INACTIVE);

    bookRepository.save(book);
    this.sendAdminBookNotification("reject", null);
  }

  @Override
  public Book getById(long id) {
    log.info("Request get book by id, bookId={}", id);
    Optional<Book> bookOptional = bookRepository.findById(id);
    if (!bookOptional.isPresent()) {
      throw new ResourceNotFoundException("Not found book with id = " + id);
    }
    return bookOptional.get();
  }

  @Override
  public void remove(long id) {
    // check comment
    Book book = this.getById(id);
    bookRepository.delete(book);
  }

  @Override
  public ResultPaginationDTO getAll(Specification<Book> spec, Pageable pageable) {
    Page<Book> pageBook = bookRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResBook> listBook = pageBook.getContent()
        .stream().map(this::convertToResBook)
        .toList();

    rs.setResult(listBook);

    return rs;
  }

  @Override
  public ResultPaginationDTO getHomeBook(Specification<Book> spec, Pageable pageable) {
    // Kết hợp điều kiện isActive với các điều kiện khác
    Specification<Book> activeSpec = BookSpecification.activeWithFilter(spec);

    Page<Book> pageBook = bookRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResPost> listBook = pageBook.getContent()
        .stream().map(this::convertToResPost)
        .toList();

    rs.setResult(listBook);
    return rs;
  }

  @Override
  public ResultPaginationDTO getExplore(Specification<Book> spec, Pageable pageable) {
    // Kết hợp điều kiện isActive với các điều kiện khác
    Specification<Book> activeSpec = BookSpecification.activeWithFilter(spec);

    Page<Book> pageBook = bookRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResBookSearch> listBook = pageBook.getContent()
        .stream().map(this::convertToResBookSearch)
        .toList();

    rs.setResult(listBook);
    return rs;
  }

  @Override
  public ResultPaginationDTO getAllPostOfUser(String email, Pageable pageable) {
    Specification<Book> spec = BookSpecification.activeByCreator(email);

    Page<Book> pageBook = bookRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResPost> listBook = pageBook.getContent()
        .stream().map(this::convertToResPost)
        .toList();

    rs.setResult(listBook);
    return rs;
  }

  @Override
  public ResBook convertToResBook(Book book) {
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());

    return ResBook.builder()
        .id(book.getId())
        .name(book.getName())
        .description(book.getDescription())
        .image(book.getImage())
        .publishedDate(book.getPublishedDate())
        .bookFormat(book.getBookFormat())
        .bookSaleLink(book.getBookSaleLink())
        .language(book.getLanguage())
        .author(book.getAuthor())
        .status(book.getStatus())
        .categories(resCategories)
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  private ResPost convertToResPost(Book book) {
    User user = userService.getUserByEmail(book.getCreatedBy());
    List<Rating> ratings = ratingRepository.findByBookId(book.getId());

    double avgRating = ratings.stream()
        .mapToLong(Rating::getStars)
        .average()
        .orElse(0.0);
    ResFeedBack resFeedBack = new ResFeedBack();
    resFeedBack.setAverageRating(avgRating);
    resFeedBack.setRatingCount(ratings.size());
    resFeedBack.setTotalOneStar(ratingRepository.countByBookIdAndStars(book.getId(), 1));
    resFeedBack.setTotalTwoStar(ratingRepository.countByBookIdAndStars(book.getId(), 2));
    resFeedBack.setTotalThreeStar(ratingRepository.countByBookIdAndStars(book.getId(), 3));
    resFeedBack.setTotalFourStar(ratingRepository.countByBookIdAndStars(book.getId(), 4));
    resFeedBack.setTotalFiveStar(ratingRepository.countByBookIdAndStars(book.getId(), 5));
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());
    List<Comment> commentReview = commentRepository.findByBookIdAndIsRatingCommentTrue(
        book.getId());

    List<ResReviewDTO> resReviewDTOs = ratings.stream().map(rv -> {
      // Tìm comment tương ứng với rating (dựa trên userId)
      Optional<Comment> ratingComment = commentReview.stream()
          .filter(comment -> comment.getUser().getId().equals(rv.getUser().getId()))
          .findFirst();

      return ResReviewDTO.builder()
          .stars(rv.getStars())
          .ratingId(rv.getId())
          .commentId(ratingComment.map(Comment::getId).orElse(null))
          .image(rv.getUser().getImage())
          .fullName(rv.getUser().getFullName())
          .userId(rv.getUser().getId())
          .comment(ratingComment.map(Comment::getComment).orElse(null))
          .createdAt(rv.getCreatedAt())
          .updatedAt(rv.getUpdatedAt())
          .build();
    }).toList();

    ResPost.User userBook = new ResPost.User(user.getId(), user.getFullName(), user.getImage());

    return ResPost.builder()
        .bookId(book.getId())
        .name(book.getName())
        .description(book.getDescription())
        .bookImage(book.getImage())
        .publishedDate(book.getPublishedDate())
        .bookFormat(book.getBookFormat())
        .bookSaleLink(book.getBookSaleLink())
        .language(book.getLanguage())
        .author(book.getAuthor())
        .status(book.getStatus())
        .stars(resFeedBack)
        .reviews(resReviewDTOs)
        .categories(resCategories)
        .user(userBook)
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  @Override
  public ResDetailBook getBookDetail(long id) {
    Book book = bookRepository.findByIdAndStatus(id, BookStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("Not found book"));
    List<Rating> ratings = ratingRepository.findByBookId(id);
    double avgRating = ratings.stream()
        .mapToLong(Rating::getStars)
        .average()
        .orElse(0.0);
    ResFeedBack resFeedBack = new ResFeedBack();
    resFeedBack.setAverageRating(avgRating);
    resFeedBack.setRatingCount(ratings.size());
    resFeedBack.setTotalOneStar(ratingRepository.countByBookIdAndStars(id, 1));
    resFeedBack.setTotalTwoStar(ratingRepository.countByBookIdAndStars(id, 2));
    resFeedBack.setTotalThreeStar(ratingRepository.countByBookIdAndStars(id, 3));
    resFeedBack.setTotalFourStar(ratingRepository.countByBookIdAndStars(id, 4));
    resFeedBack.setTotalFiveStar(ratingRepository.countByBookIdAndStars(id, 5));
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());
    List<Comment> commentReview = commentRepository.findByBookIdAndIsRatingCommentTrue(id);

    List<ResReviewDTO> resReviewDTOs = ratings.stream().map(rv -> {
      // Tìm comment tương ứng với rating (dựa trên userId)
      Optional<Comment> ratingComment = commentReview.stream()
          .filter(comment -> comment.getUser().getId().equals(rv.getUser().getId()))
          .findFirst();

      return ResReviewDTO.builder()
          .stars(rv.getStars())
          .ratingId(rv.getId())
          .commentId(ratingComment.map(Comment::getId).orElse(null))
          .image(rv.getUser().getImage())
          .fullName(rv.getUser().getFullName())
          .userId(rv.getUser().getId())
          .comment(ratingComment.map(Comment::getComment).orElse(null))
          .createdAt(rv.getCreatedAt())
          .updatedAt(rv.getUpdatedAt())
          .build();
    }).toList();
    return ResDetailBook.builder()
        .id(book.getId())
        .name(book.getName())
        .description(book.getDescription())
        .image(book.getImage())
        .publishedDate(book.getPublishedDate())
        .bookFormat(book.getBookFormat())
        .bookSaleLink(book.getBookSaleLink())
        .language(book.getLanguage())
        .author(book.getAuthor())
        .status(book.getStatus())
        .stars(resFeedBack)
        .reviews(resReviewDTOs)
        .categories(resCategories)
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }
  @Override
  public ResBookSearch convertToResBookSearch(Book book){
    List<Rating> ratings = ratingRepository.findByBookId(book.getId());

    double avgRating = ratings.stream()
        .mapToLong(Rating::getStars)
        .average()
        .orElse(0.0);

    return ResBookSearch.builder()
        .id(book.getId())
        .name(book.getName())
        .author(book.getAuthor())
        .image(book.getImage())
        .averageRating(avgRating)
        .ratingCount(ratings.size())
        .publishedDate(book.getPublishedDate())
        .build();
  }

  @Override
  public ResultPaginationDTO searchHome(Pageable pageable, String keyword) {
    Specification<Book> spec = BookSpecification.search(keyword);

    Page<Book> pageBook = bookRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResBookSearch> listBook = pageBook.getContent()
        .stream().map(this::convertToResBookSearch)
        .toList();

    rs.setResult(listBook);
    return rs;
  }
}
