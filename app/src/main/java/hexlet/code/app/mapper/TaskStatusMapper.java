package hexlet.code.app.mapper;

import hexlet.code.app.DTO.*;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.*;

@Mapper(
        // Подключение JsonNullableMapper
        uses = { JsonNullableMapper.class },
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
