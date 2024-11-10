package hexlet.code.app.component;
import hexlet.code.app.DTO.UserCreateDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserPerository;


import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import net.datafaker.Faker;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserPerository userRepository;

    @Autowired
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        var faker = new Faker();
//        var email = "hexlet@example.com";
//        var userData = new UserCreateDTO();
//        userData.setEmail(email);
//        userData.setPassword("qwerty");
//        userData.setFirstName(faker.name().firstName());
//        userData.setLastName(faker.name().lastName());
//        userService.create(userData);
    }
}
