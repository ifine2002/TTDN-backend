package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResAdminBookDTO;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResDetailBook;
import vn.ifine.dto.response.ResPost;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Book;
import vn.ifine.service.BookService;

@RestController
@RequestMapping("/book")
@Slf4j(topic = "BOOK-CONTROLLER")
@Validated
@Tag(name = "Book Controller")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ResBook>> create(
      @Valid ReqBookDTO reqBookDTO) {
    log.info("Request create book, {}", reqBookDTO.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a book success",
            this.bookService.create(reqBookDTO)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResBook>> getUserById(@PathVariable @Min(1) long id) {
    log.info("Request get book, id={}", id);
    Book book = bookService.getById(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a book success",
            bookService.convertToResBook(book)));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ResBook>> update(@PathVariable @Min(1) long id,
      @Valid ReqBookDTO reqBookDTO) {
    log.info("Request update book, id={}", id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a book success",
            bookService.update(id, reqBookDTO)));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") @Min(1) long id) {
    log.info("Request remove book, id={}", id);
    bookService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a book success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllBook(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all book success",
            this.bookService.getAll(spec, pageable)));
  }

  @GetMapping("/home-page")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getHomeBook(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch home book success",
            this.bookService.getHomeBook(spec, pageable)));
  }

  @GetMapping("/explore")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getExplore(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch explore book success",
            this.bookService.getExplore(spec, pageable)));
  }

  @GetMapping("/list-book-user")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllPostOfUser(@RequestParam String email, Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all post of user success",
            this.bookService.getAllPostOfUser(email, pageable)));
  }

  @PostMapping(value = "/upload-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ResAdminBookDTO>> uploadPost(
      @Valid ReqBookDTO reqBookDTO, Principal principal) {
    log.info("Request upload book, {}", reqBookDTO.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a book success",
            this.bookService.uploadBook(reqBookDTO, principal.getName())));
  }

  @GetMapping("/list-none")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getApproveBooks(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all book is none success",
            this.bookService.getApproveBooks(spec, pageable)));
  }

  @GetMapping("/detail-book/{id}")
  public ResponseEntity<ApiResponse<ResDetailBook>> getDetailBook(@PathVariable @Min(1) long id) {
    log.info("Request get book, id={}", id);
    ResDetailBook detailBook = bookService.getBookDetail(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a book success",
            detailBook));
  }

  @PatchMapping("/approve/{id}")
  public ResponseEntity<ApiResponse<Void>> approveBook(@PathVariable("id") @Min(1) Long bookId){
    bookService.approveBook(bookId);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Approve a book success",
            null));
  }

  @PatchMapping("/reject/{id}")
  public ResponseEntity<ApiResponse<Void>> rejectBook(@PathVariable("id") @Min(1) Long bookId){
    bookService.rejectBook(bookId);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Reject a book success",
            null));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<?>> searchHome(@RequestParam String keyword, Pageable pageable) {
    log.info("Request search book in home page, keyword={}", keyword);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Search book success",
            bookService.searchHome(pageable, keyword)));
  }
}
