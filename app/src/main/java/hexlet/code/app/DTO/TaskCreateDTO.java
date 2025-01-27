package hexlet.code.app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class TaskCreateDTO {
    private Long index;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String  status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private Set<Long> taskLabelIds;
}
