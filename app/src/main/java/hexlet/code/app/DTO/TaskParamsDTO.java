package hexlet.code.app.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskParamsDTO {
    private Long assigneeId;
    private String status;
}
