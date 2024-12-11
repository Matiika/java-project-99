package hexlet.code.app.controller.api;

import hexlet.code.app.DTO.UserDTO;
import hexlet.code.app.DTO.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;


import org.assertj.core.api.Assertions;
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
    private UserRepository userRepository; // Репозиторий для работы с БД
    @Autowired
    private ModelGenerator modelGenerator; // Генератор моделей данных
    @Autowired
    private ObjectMapper om; // Для сериализации/десериализации JSON
    @Autowired
    private UserMapper userMapper; // Для преобразования User <-> UserDTO

    private JwtRequestPostProcessor token; // Для имитации JWT токена в запросах
    private User testUser; // Тестовый пользователь для использования в тестах


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        System.out.println("Response Body: " + body);


        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<>() { });

        var actual = userDTOS.stream().map(userMapper::map).toList();
        var expected = userRepository.findAll();

        Assertions.assertThat(actual).hasSize(expected.size());

// Validate the contents of the list
        for (int i = 0; i < actual.size(); i++) {
            Assertions.assertThat(actual.get(i).getEmail()).isEqualTo(expected.get(i).getEmail());
            Assertions.assertThat(actual.get(i).getFirstName()).isEqualTo(expected.get(i).getFirstName());
            Assertions.assertThat(actual.get(i).getLastName()).isEqualTo(expected.get(i).getLastName());
        }

        System.out.println("Expected: " + body);
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserCreateDTOModel())
                .create();

        var request = post("/api/users")
                .with(token)
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
        // Создаем тестового пользователя
        var user = Instancio.of(modelGenerator.getUserModel())
                .create();

        // Сохраняем в базу и запоминаем ID
        var savedUser = userRepository.save(user);
        long userId = savedUser.getId();

        // Подготавливаем данные для обновления
        UserDTO dto = userMapper.map(user);
        dto.setFirstName("Mike");

        // Выполняем запрос, используя явное указание ID в URL
        mockMvc.perform(put("/api/users/{id}", userId)
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        token = jwt().jwt(builder -> builder.subject(savedUser.getEmail()));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());


        // Проверяем обновление, явно используя ID для поиска
        var updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getId()).isEqualTo(userId); // Явно проверяем что ID не изменился
        assertThat(updatedUser.getFirstName()).isEqualTo("Mike");
    }

    @Test
    public void testShow() throws Exception {
        var user = Instancio.of(modelGenerator.getUserModel())
                .create();

        userRepository.save(user);

        var request = get("/api/users/" + user.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("username").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName()),
                v -> v.node("lastName").isEqualTo(user.getLastName()));
    }

}
