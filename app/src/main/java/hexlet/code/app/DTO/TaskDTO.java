package hexlet.code.app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private Long index;
    private String title;
    private String content;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private LocalDate createdAt;
    private Set<Long> taskLabelIds;
}