package hexlet.code.util;

import hexlet.code.DTO.TaskCreateDTO;
import hexlet.code.DTO.TaskStatusCreateDTO;
import hexlet.code.DTO.TaskStatusUpdateDTO;
import hexlet.code.DTO.TaskUpdateDTO;
import hexlet.code.DTO.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<Task> taskModel;
    private Model<TaskCreateDTO> taskCreateDTOModel;
    private Model<TaskUpdateDTO> taskUpdateDTOModel;
    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(3, 100))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password(3, 100))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getTitle), () -> faker.lorem().sentence())
                .supply(Select.field(Task::getContent), () -> faker.lorem().paragraph())
                .supply(Select.field(Task::getIndex), () -> faker.number().randomNumber())
                .set(Select.field(Task::getLabels), new HashSet<>())
                .ignore(Select.field(Task::getCreatedAt))
                .toModel();

        taskCreateDTOModel = Instancio.of(TaskCreateDTO.class)
                .supply(Select.field(TaskCreateDTO::getTitle), () -> faker.lorem().sentence())
                .supply(Select.field(TaskCreateDTO::getContent), () -> faker.lorem().paragraph())
                .supply(Select.field(TaskCreateDTO::getIndex), () -> faker.number().randomNumber())
                .supply(Select.field(TaskCreateDTO::getStatus), () -> "draft")
                .set(Select.field(TaskCreateDTO::getTaskLabelIds), new HashSet<>())
                .toModel();

        taskUpdateDTOModel = Instancio.of(TaskUpdateDTO.class)
                .supply(Select.field(TaskUpdateDTO::getTitle), () -> JsonNullable.of(faker.lorem().sentence()))
                .supply(Select.field(TaskUpdateDTO::getContent), () -> JsonNullable.of(faker.lorem().paragraph()))
                .supply(Select.field(TaskUpdateDTO::getIndex), () -> JsonNullable.of(faker.number().randomNumber()))
                .supply(Select.field(TaskUpdateDTO::getStatus), () -> JsonNullable.of("draft"))
                .set(Select.field(TaskUpdateDTO::getTaskLabelIds), JsonNullable.of(new HashSet<>()))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> faker.lorem().word())
                .ignore(Select.field(Label::getCreatedAt))
                .toModel();

        taskCreateDTOModel = Instancio.of(TaskCreateDTO.class)
                .supply(Select.field(TaskCreateDTO::getTitle), () -> faker.lorem().sentence())
                .supply(Select.field(TaskCreateDTO::getContent), () -> faker.lorem().paragraph())
                .supply(Select.field(TaskCreateDTO::getIndex), () -> faker.number().randomNumber())
                .supply(Select.field(TaskCreateDTO::getStatus), () -> "draft")
                .supply(Select.field(TaskCreateDTO::getAssigneeId), () ->
                        userRepository.findByEmail("hexlet@example.com")
                                .map(User::getId)
                                .orElse(null))
                .set(Select.field(TaskCreateDTO::getTaskLabelIds), new HashSet<>())
                .toModel();
    }

    public Model<TaskStatus> getTaskStatusModel() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.name().title())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();
    }

    public Model<TaskStatusCreateDTO> getTaskStatusCreateDTOModel() {
        return Instancio.of(TaskStatusCreateDTO.class)
                .supply(Select.field(TaskStatusCreateDTO::getName), () -> faker.name().title())
                .supply(Select.field(TaskStatusCreateDTO::getSlug), () -> faker.internet().slug())
                .toModel();
    }

    public Model<TaskStatusUpdateDTO> getTaskStatusUpdateDTOModel() {
        return Instancio.of(TaskStatusUpdateDTO.class)
                .supply(Select.field(TaskStatusUpdateDTO::getName),
                        () -> JsonNullable.of(faker.name().title()))
                .supply(Select.field(TaskStatusUpdateDTO::getSlug),
                        () -> JsonNullable.of(faker.internet().slug()))
                .toModel();
    }
}
