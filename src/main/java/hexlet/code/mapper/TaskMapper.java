package hexlet.code.mapper;

import hexlet.code.DTO.TaskCreateDTO;
import hexlet.code.DTO.TaskDTO;
import hexlet.code.DTO.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;



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

    @Autowired
    private LabelRepository labelRepository;

    // Маппинг из DTO в сущность
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "labelIdsToLabels")
    public abstract Task map(TaskCreateDTO taskCreateDTO);

    // Маппинг из сущности в DTO
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToLabelIds")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "labelIdsToLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);



    @Named("slugToStatus")
    protected TaskStatus slugToStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Status with slug '%s' not found", slug)
                ));
    }

    @Named("labelIdsToLabels")
    protected Set<Label> labelIdsToLabels(Set<Long> labelIds) {
        if (labelIds == null) {
            return new LinkedHashSet<>();
        }
        return labelIds.stream()
                .map(labelId -> labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label not found")))
                .collect(Collectors.toSet());
    }

    @Named("labelsToLabelIds")
    protected Set<Long> labelsToLabelIds(Set<Label> labels) {
        if (labels == null) {
            return new LinkedHashSet<>();
        }
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
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
