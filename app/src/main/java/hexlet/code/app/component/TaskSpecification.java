package hexlet.code.app.component;

import hexlet.code.app.DTO.TaskParamsDTO;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withStatus(params.getStatus()));
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> assigneeId == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, criteriaBuilder) -> status == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("taskStatus").get("name"), status);
    }

}
