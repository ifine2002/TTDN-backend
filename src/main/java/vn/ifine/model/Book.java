package vn.ifine.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ifine.util.BookStatus;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends AbstractModel<Long>{

  @Column(name = "name")
  private String name;

  @Column(name = "description", columnDefinition = "MEDIUMTEXT")
  private String description;

  @Column(name = "image")
  private String image;

  @Column(name = "published_date")
  private LocalDate publishedDate;

  @Column(name = "book_format")
  private String bookFormat;

  @Column(name = "book_sale_link")
  private String bookSaleLink;

  @Column(name = "language")
  private String language;

  // Not a user just book's info
  @Column(name = "author")
  private String author;

  @Column(name = "status")
  private BookStatus status;

  @ManyToMany
  @JoinTable(name = "book_category", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories;

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;
}
