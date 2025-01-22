package hexlet.code.app.mapper;

import hexlet.code.app.DTO.label.LabelCreateDTO;
import hexlet.code.app.DTO.label.LabelDTO;
import hexlet.code.app.DTO.label.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)


public abstract class LabelMapper {
    public abstract Label createLabel(LabelCreateDTO labelCreateDTO);
    public abstract LabelDTO fromEntityToDTO(Label label);
    public abstract Label fromDtoToEntity(LabelDTO labelDTO);
    public abstract void update(LabelUpdateDTO labelUpdateDTO, @MappingTarget Label label);
}
