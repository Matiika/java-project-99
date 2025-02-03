package hexlet.code.component;


import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CustomUserDetailsService userService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Сначала создаем статусы
        createDefaultStatuses();
        // Создаем пользователей
        createDefaultUser();
        // Создаем лейблы
        createDefaultLabels();
    }

    private void createDefaultStatuses() {
        var defaultStatuses = new String[][]{
                {"Draft", "draft"},
                {"To Review", "to_review"},
                {"To Be Fixed", "to_be_fixed"},
                {"To Publish", "to_publish"},
                {"Published", "published"}
        };

        for (String[] status : defaultStatuses) {
            var statusName = status[0];
            var statusSlug = status[1];

            if (taskStatusRepository.findBySlug(statusSlug).isEmpty()) {
                var taskStatus = new TaskStatus();
                taskStatus.setName(statusName);
                taskStatus.setSlug(statusSlug);
                taskStatusRepository.save(taskStatus);
            }
        }
    }

    private void createDefaultLabels() {
        var defaultLabels = new String[]{
                "feature", "bug"
        };

        for (String labelName : defaultLabels) {
           if (labelRepository.findByName(labelName).isEmpty()) {
                var label = new Label();
                label.setName(labelName);
                labelRepository.save(label);
            }
        }
    }

    private void createDefaultUser() {
        var faker = new Faker();
        var userData = new UserCreateDTO();
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        userData.setFirstName(faker.name().firstName());
        userData.setLastName(faker.name().lastName());

        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var user = userMapper.map(userData);
            userRepository.save(user);
        }
    }
}

//@Component
//@AllArgsConstructor
//public class DataInitializer implements ApplicationRunner {
//
//    @Autowired
//    private final CustomUserDetailsService userService;
//
//    @Autowired
//    private final UserRepository userRepository;
//
//    @Autowired
//    private final UserMapper userMapper;
//
//    @Autowired
//    private final TaskStatusRepository taskStatusRepository;
//
//
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        var faker = new Faker();
//        var userData = new UserCreateDTO();
//        userData.setEmail("hexlet@example.com");
//        userData.setPassword("qwerty");
//        userData.setFirstName(faker.name().firstName());
//        userData.setLastName(faker.name().lastName());
//        if (!userRepository.findByEmail("hexlet@example.com").isPresent()) {
//            var user = userMapper.map(userData);
//            userRepository.save(user);
//        }
//
//        var defaultStatuses = new String[][]{
//                {"Draft", "draft"},
//                {"To Review", "to_review"},
//                {"To Be Fixed", "to_be_fixed"},
//                {"To Publish", "to_publish"},
//                {"Published", "published"}
//        };
//
//        // Создаем статусы если они еще не существуют
//        for (String[] status : defaultStatuses) {
//            var statusName = status[0];
//            var statusSlug = status[1];
//
//            if (taskStatusRepository.findBySlug(statusSlug).isEmpty()) {
//                var taskStatus = new TaskStatus();
//                taskStatus.setName(statusName);
//                taskStatus.setSlug(statusSlug);
//                taskStatusRepository.save(taskStatus);
//            }
//        }
//    }
//}
