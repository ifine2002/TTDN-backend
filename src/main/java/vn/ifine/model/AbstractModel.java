package vn.ifine.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.ifine.service.impl.JwtServiceImpl;


@Getter
@Setter
@MappedSuperclass
public abstract class AbstractModel<T extends Serializable> implements Serializable {

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

