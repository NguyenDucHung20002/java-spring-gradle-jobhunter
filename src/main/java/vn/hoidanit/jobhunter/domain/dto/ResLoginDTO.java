package vn.hoidanit.jobhunter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ResLoginDTO {
  private String accessToken;
  private UserLogin userLogin;

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public class UserLogin {
    private long id;
    private String email;
    private String name;
  }


}
