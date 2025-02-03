package hexlet.code.controller.api;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;

import org.assertj.core.api.Assertions;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    private JwtRequestPostProcessor token;
    private Task testTask;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        // Clear previous test data
        taskRepository.deleteAll();

        // Create a task with proper relationships
        var user = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("Default user not found"));
        var taskStatus = taskStatusRepository.findBySlug("draft")
                .orElseThrow(() -> new RuntimeException("Default status not found"));

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();

        testTask.setAssignee(user);
        testTask.setTaskStatus(taskStatus);

        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();
        List<TaskDTO> tasks = om.readValue(body, new TypeReference<>() { });

        assertThat(tasks).isNotEmpty();
        assertThat(response.getHeader("X-Total-Count"))
                .isEqualTo(String.valueOf(taskRepository.count()));
    }

    @Test
    public void testIndexWithParams() throws Exception {
        var params = new TaskParamsDTO();
        params.setTitleCont(testTask.getTitle().substring(1, 4));
        params.setAssigneeId(testTask.getAssignee().getId());

        var response = mockMvc.perform(get("/api/tasks")
                        .with(token)
                        .param("titleCont", params.getTitleCont())
                        .param("assigneeId", params.getAssigneeId().toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();
        List<TaskDTO> tasks = om.readValue(body, new TypeReference<>() { });

        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getTitle()).contains(params.getTitleCont());
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/{id}", testTask.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getTitle()),
                v -> v.node("content").isEqualTo(testTask.getContent()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var user = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("Default user not found"));

        var taskData = Instancio.of(modelGenerator.getTaskCreateDTOModel())
                .create();

        taskData.setAssigneeId(user.getId());

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskData));

        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();
        TaskDTO task = om.readValue(body, TaskDTO.class);

        assertThat(task).isNotNull();
        assertThat(task.getTitle()).isEqualTo(taskData.getTitle());
        assertThat(task.getContent()).isEqualTo(taskData.getContent());
        assertThat(task.getAssigneeId()).isEqualTo(user.getId());

        Assertions.assertThat(taskRepository.findById(task.getId())).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        var user = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("Default user not found"));

        var taskData = Instancio.of(modelGenerator.getTaskUpdateDTOModel())
                .create();
        taskData.setAssigneeId(JsonNullable.of(user.getId()));


        var request = put("/api/tasks/{id}", testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updated = taskRepository.findById(testTask.getId()).orElseThrow();
        Assertions.assertThat(updated.getTitle()).isEqualTo(taskData.getTitle().get());
        Assertions.assertThat(updated.getAssignee().getId()).isEqualTo(user.getId());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/tasks/{id}", testTask.getId())
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isFalse();
    }

    @Test
    public void testWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/tasks"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/tasks/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isUnauthorized());
    }
}
