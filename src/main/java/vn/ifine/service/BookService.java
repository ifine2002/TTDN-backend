package vn.ifine.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ResAdminBookDTO;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResBookSearch;
import vn.ifine.dto.response.ResDetailBook;
import vn.ifine.dto.response.ResPost;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Book;

@Service
public interface BookService {
  //admin
  ResBook create(ReqBookDTO reqBookDTO);

  //user post
  ResAdminBookDTO uploadBook(ReqBookDTO reqBookDTO, String email);

  ResultPaginationDTO getApproveBooks(Specification<Book> spec, Pageable pageable);

  ResBook update(long bookId, ReqBookDTO reqBookDTO);

  void approveBook(Long bookId);

  void rejectBook(Long bookId);

  Book getById(long id);

  void remove(long id);

  ResultPaginationDTO getAll(Specification<Book> spec, Pageable pageable);

  ResultPaginationDTO getHomeBook(Specification<Book> spec, Pageable pageable);

  ResultPaginationDTO getExplore(Specification<Book> spec, Pageable pageable);

  ResultPaginationDTO getAllPostOfUser(String email, Pageable pageable);

  ResBook convertToResBook(Book book);

  ResDetailBook getBookDetail(long id);

  ResultPaginationDTO searchHome(Pageable pageable, String keyword);

  ResBookSearch convertToResBookSearch(Book book);
}
