package hexlet.code.app.DTO;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

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
    private JsonNullable<Long> assignee;
}
