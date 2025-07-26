package vn.ifine.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends AbstractEntity<Long>{

  private String name;

  private String apiPath;

  private String method;

  private String module;

  @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Role> roles;

  public Permission(String name, String apiPath, String method, String module) {
    this.name = name;
    this.apiPath = apiPath;
    this.module = module;
    this.method = method;
  }
}

