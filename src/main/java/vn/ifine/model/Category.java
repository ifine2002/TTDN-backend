package vn.ifine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "categories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends AbstractEntity<Integer>{
  @Column(name = "name")
  private String name;

  @Column(name = "description", columnDefinition = "MEDIUMTEXT")
  private String description;

  @Column(name = "image")
  private String image;

  @ManyToMany(mappedBy = "categories")
  private Set<Book> books;

}
