package hexlet.code.controller.api;

import hexlet.code.DTO.label.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.assertj.core.api.Assertions;
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
import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private Faker faker;

    private JwtRequestPostProcessor token;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        // Очищаем репозиторий перед каждым тестом
        labelRepository.deleteAll();

        // Создаем тестовую метку с уникальным именем
        testLabel = new Label();
        testLabel.setName(faker.name().title());
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();
        List<LabelDTO> labelDTOs = om.readValue(body, new TypeReference<>() { });

        assertThat(response.getHeader("X-Total-Count"))
                .isEqualTo(String.valueOf(labelRepository.count()));

        var actual = labelDTOs.stream()
                .map(labelMapper::fromDtoToEntity)
                .toList();
        var expected = labelRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/{id}", testLabel.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", testLabel.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var newLabelName = faker.name().title();
        var newLabelData = "{\"name\": \"" + newLabelName + "\"}";

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLabelData);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var label = labelRepository.findByName(newLabelName).orElse(null);

        assertNotNull(label);
        assertThat(label.getName()).isEqualTo(newLabelName);
    }

    @Test
    public void testCreateWithoutAuth() throws Exception {
        var newLabelData = "{\"name\": \"" + faker.name().title() + "\"}";

        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLabelData);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        var newName = faker.name().title();
        var updateData = "{\"name\": \"" + newName + "\"}";

        var request = put("/api/labels/{id}", testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updated = labelRepository.findById(testLabel.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo(newName);
    }

    @Test
    public void testUpdateWithoutAuth() throws Exception {
        var updateData = "{\"name\": \"" + faker.name().title() + "\"}";

        var request = put("/api/labels/{id}", testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateData);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/labels/{id}", testLabel.getId())
                .with(token);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(labelRepository.existsById(testLabel.getId())).isFalse();
    }

    @Test
    public void testDeleteWithoutAuth() throws Exception {
        var request = delete("/api/labels/{id}", testLabel.getId());

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertThat(labelRepository.existsById(testLabel.getId())).isTrue();
    }
}
