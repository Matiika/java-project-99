package hexlet.code.app.component;

import hexlet.code.app.DTO.TaskParamsDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withStatus(params.getStatus()))
                .and(withTitleCont(params.getTitleCont()))
                .and(withLabelId(params.getLabelId()));
    }

//    private Specification<Task> withTitleCont(String titleCont) {
//        return (root, query, criteriaBuilder) -> titleCont == null ?
//                criteriaBuilder.conjunction() :
//                criteriaBuilder.like(root.get("title"), "%" + titleCont + "%");
//    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, criteriaBuilder) -> titleCont == null ?
                criteriaBuilder.conjunction() :
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + titleCont.toLowerCase() + "%");
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> assigneeId == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, criteriaBuilder) -> status == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, criteriaBuilder) -> labelId == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.join("labels", JoinType.INNER).get("id"), labelId);
    }

}
