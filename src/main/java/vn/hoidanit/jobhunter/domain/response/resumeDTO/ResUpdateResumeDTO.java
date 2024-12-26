package vn.hoidanit.jobhunter.domain.response.resumeDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResUpdateResumeDTO {
    private Instant updatedAt;
    private String updatedBy;
}
