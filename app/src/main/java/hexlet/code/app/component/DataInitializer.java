package hexlet.code.app.component;


import hexlet.code.app.DTO.UserCreateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final CustomUserDetailsService userService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var faker = new Faker();
        var userData = new UserCreateDTO();
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        userData.setFirstName(faker.name().firstName());
        userData.setLastName(faker.name().lastName());
        if (!userRepository.findByEmail("hexlet@example.com").isPresent()) {
            var user = userMapper.map(userData);
            userRepository.save(user);
        }
    }
}
