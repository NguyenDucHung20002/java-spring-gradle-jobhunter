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
public class ResCreateUserDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createAt;
    private CompanyUser company;

    @Setter
    @Getter
    public static class CompanyUser {
        private Long id;
        private String name;
    }

}
