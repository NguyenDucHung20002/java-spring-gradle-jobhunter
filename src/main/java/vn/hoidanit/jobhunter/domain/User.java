package vn.hoidanit.jobhunter.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  @NotBlank(message = "Email không được để trống")
  private String email;
  @NotBlank(message = "Password không được để trống")
  private String password;
  private int age;
  @Enumerated(EnumType.STRING)
  private GenderEnum gender;
  private String address;
  
  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;
  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;
  private String updatedBy;


  @PrePersist
  public void handleBeforeCreate() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
            ? SecurityUtil.getCurrentUserLogin().get()
            : "";

    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
            ? SecurityUtil.getCurrentUserLogin().get()
            : "";

    this.updatedAt = Instant.now();
  }
}