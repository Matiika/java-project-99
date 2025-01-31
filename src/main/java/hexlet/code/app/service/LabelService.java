package hexlet.code.app.service;

import hexlet.code.app.DTO.label.LabelCreateDTO;
import hexlet.code.app.DTO.label.LabelDTO;
import hexlet.code.app.DTO.label.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelRepository labelRepository;

    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();
        return labels.stream()
                .map(label -> labelMapper.fromEntityToDTO(label))
                .toList();
    }

    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        var label = labelMapper.createLabel(labelCreateDTO);
        var savedLabel = labelRepository.save(label);
        var labelDto = labelMapper.fromEntityToDTO(savedLabel);
        return labelDto;
    }

    public LabelDTO show(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return labelMapper.fromEntityToDTO(label);
    }

    public LabelDTO update(LabelUpdateDTO labelUpdateDTO, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        labelMapper.update(labelUpdateDTO, label);
        labelRepository.save(label);
        return labelMapper.fromEntityToDTO(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
