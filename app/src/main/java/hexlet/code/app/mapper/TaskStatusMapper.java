package hexlet.code.app.mapper;

import hexlet.code.app.DTO.TaskStatusCreateDTO;
import hexlet.code.app.DTO.TaskStatusDTO;
import hexlet.code.app.DTO.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskStatusMapper {

    public abstract TaskStatus map(TaskStatusCreateDTO model);

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract TaskStatus map(TaskStatusDTO model);

    public abstract void update(TaskStatusUpdateDTO update, @MappingTarget TaskStatus destination);
}
