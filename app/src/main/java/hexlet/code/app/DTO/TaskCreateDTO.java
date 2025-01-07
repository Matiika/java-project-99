package hexlet.code.app.DTO;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private Long index;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String  status;

    private Long assigneeId;
}
