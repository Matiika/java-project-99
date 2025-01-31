package hexlet.code.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {

    @NotNull
    private JsonNullable<Long> index;

    @NotNull
    private JsonNullable<String> title;

    @NotNull
    private JsonNullable<String> content;

    @NotNull
    private JsonNullable<String> status;

    @NotNull
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private JsonNullable<Set<Long>> taskLabelIds;
}
