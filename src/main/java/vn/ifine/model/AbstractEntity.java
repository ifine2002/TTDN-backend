package vn.ifine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.util.Date;
import vn.ifine.service.impl.JwtServiceImpl;


@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  T id;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "created_at")
  @CreationTimestamp
  private Date createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private Date updatedAt;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @PrePersist
  public void handleBeforeCreate() {
    this.createdBy = JwtServiceImpl.getCurrentUserLogin().isPresent()
        ? JwtServiceImpl.getCurrentUserLogin().get()
        : "";
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedBy = JwtServiceImpl.getCurrentUserLogin().isPresent()
        ? JwtServiceImpl.getCurrentUserLogin().get()
        : "";
  }
}

