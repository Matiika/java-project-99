package hexlet.code.dto.label;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
