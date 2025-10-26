package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResFavoriteBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.FavoriteBook;

@Service
public interface FavoriteBookService {

  ResFavoriteBook createFavoriteBook(Long bookId, String email);

  void deleteFavoriteBook(Long favoriteId, String email);

  ResultPaginationDTO getBookFavoriteOfUser(Long userId, Specification<FavoriteBook> spec, Pageable pageable);

  ResultPaginationDTO getListFavoriteOfUser(String email, Specification<FavoriteBook> spec, Pageable pageable);
}
