package hexlet.code.app.service;

import hexlet.code.app.DTO.UserCreateDTO;
import hexlet.code.app.DTO.UserDTO;
import hexlet.code.app.DTO.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserPerository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserPerository userPerository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        var users = userPerository.findAll()
                .stream()
                .map(user -> userMapper.map(user))
                .toList();
        return users;
    }

    public UserDTO findById(Long id) {
        var user = userPerository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO userCreateDTO) {
        var user = userMapper.map(userCreateDTO);
        userPerository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, Long id) {
        var user = userPerository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        userMapper.update(userUpdateDTO, user);
        userPerository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        userPerository.deleteById(id);
    }
}
