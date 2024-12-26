package vn.hoidanit.jobhunter.domain.response.userDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private Long id;
        private String name;
    }
}
