package hexlet.code.app.controller;

import hexlet.code.app.DTO.UserCreateDTO;
import hexlet.code.app.DTO.UserDTO;
import hexlet.code.app.DTO.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.CustomUserDetailsService;
import hexlet.code.app.util.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final CustomUserDetailsService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final UserUtils userUtils;

    @GetMapping
    public ResponseEntity<List<UserDTO>> index() {
        return userService.index();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return userMapper.map(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserCreateDTO userCreateDTO) {
        return userService.create(userCreateDTO);
    }

//    @PostMapping(path = "")
//    @ResponseStatus(HttpStatus.CREATED)
//    public UserDTO create(@RequestBody UserCreateDTO userCreateDTO) {
//        var user = userMapper.map(userCreateDTO);
//        System.out.println("Email before save: " + user.getEmail());
//        userRepository.save(user);
//        System.out.println("Email after save: " + user.getEmail());
//        System.out.println("Email with Mapper: " + userMapper.map(user).getUsername());
//        return userMapper.map(user);
//    }

//    @PutMapping(path = "/{id}")
//    @PreAuthorize(ONLY_OWNER_BY_ID)
//    @ResponseStatus(HttpStatus.OK)
//    public UserDTO update(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Long id) {
//        var user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
//        userMapper.update(userUpdateDTO, user);
//        userRepository.save(user);
//        return userMapper.map(user);
//    }
//
//    @DeleteMapping(path = "/{id}")
//    @PreAuthorize(ONLY_OWNER_BY_ID)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        userRepository.deleteById(id);
//    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Long id) {
        return userService.update(userUpdateDTO, id);
    }

//    @PutMapping(path = "/{id}")
//    @PreAuthorize(ONLY_OWNER_BY_ID)
//    @ResponseStatus(HttpStatus.OK)
//    public UserDTO update(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Long id) {
//        var user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Получаем текущего аутентифицированного пользователя
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//
//        // Проверяем, что текущий пользователь является владельцем профиля
//        if (!currentUsername.equals(user.getUsername())) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You do not have permission to update this user"
//            );
//        }
//
//        userMapper.update(userUpdateDTO, user);
//        userRepository.save(user);
//        return userMapper.map(user);
//    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    //@PreAuthorize(value = "@userUtils.getCurrentUser().getEmail() == @userRepository.findById(#id).orElseThrow().getEmail()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

//        // Получаем текущего аутентифицированного пользователя
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//
//        // Проверяем, что текущий пользователь является владельцем профиля
//        if (!currentUsername.equals(user.getEmail())) {
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You do not have permission to delete this user"
//            );
//        }

        userRepository.deleteById(id);
    }

}
