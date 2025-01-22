package hexlet.code.app.mapper;

import hexlet.code.app.DTO.TaskCreateDTO;
import hexlet.code.app.DTO.TaskDTO;
import hexlet.code.app.DTO.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    // Маппинг из DTO в сущность
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    public abstract Task map(TaskCreateDTO taskCreateDTO);

    // Маппинг из сущности в DTO
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);



    @Named("slugToStatus")
    protected TaskStatus slugToStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Status with slug '%s' not found", slug)
                ));
    }

//    @Named("updateStatus")
//    protected TaskStatus updateStatus(JsonNullable<String> statusSlug) {
//        if (statusSlug == null || !statusSlug.isPresent()) {
//            return null;
//        }
//        return taskStatusRepository.findBySlug(statusSlug.get())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        String.format("Status with slug '%s' not found", statusSlug.get())
//                ));
//    }
//
//    // Или еще проще:
//    @Named("updateStatus")
//    protected TaskStatus updateStatus(JsonNullable<String> statusSlug) {
//        return statusSlug != null && statusSlug.isPresent()
//                ? taskStatusRepository.findBySlug(statusSlug.get())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        String.format("Status with slug '%s' not found", statusSlug.get())
//                ))
//                : null;
//    }
}
