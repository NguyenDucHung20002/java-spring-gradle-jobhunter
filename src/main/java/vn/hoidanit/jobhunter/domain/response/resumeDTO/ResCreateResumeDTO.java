package vn.hoidanit.jobhunter.domain.response.resumeDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Setter
@Getter
public class ResCreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
