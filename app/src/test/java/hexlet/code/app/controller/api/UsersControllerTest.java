package hexlet.code.app.controller.api;

import hexlet.code.app.DTO.UserDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserPerository;
import hexlet.code.app.util.ModelGenerator;

import com.fasterxml.jackson.core.type.TypeReference;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.HashMap;


import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private WebApplicationContext wac; // Контекст Spring Web приложения
    @Autowired
    private MockMvc mockMvc; // Используется для имитации HTTP запросов
    @Autowired
    private Faker faker; // Библиотека для генерации тестовых данных
    @Autowired
    private UserPerository userRepository; // Репозиторий для работы с БД
    @Autowired
    private ModelGenerator modelGenerator; // Генератор моделей данных
    @Autowired
    private ObjectMapper om; // Для сериализации/десериализации JSON
    @Autowired
    private UserMapper userMapper; // Для преобразования User <-> UserDTO

    private JwtRequestPostProcessor token; // Для имитации JWT токена в запросах
    private User testUser; // Тестовый пользователь для использования в тестах

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> actual = om.readValue(body, new TypeReference<>() { });
        var expected = userRepository.findAll().stream()
                .map(userMapper::map)  // преобразуем User в UserDTO
                .toList();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserCreateDTOModel())
                .create();

        var request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).get();

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testUpdate() throws Exception {

        var user = Instancio.of(modelGenerator.getUserModel())
                .create();

        userRepository.save(user);

        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userWithUpdate = userRepository.findById(user.getId()).get();
        assertThat(userWithUpdate.getFirstName()).isEqualTo(("Mike"));
    }

    @Test
    public void testShow() throws Exception {
        var user = Instancio.of(modelGenerator.getUserModel())
                .create();

        userRepository.save(user);

        var request = get("/users/" + user.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName()),
                v -> v.node("lastName").isEqualTo(user.getLastName()));
    }

}
