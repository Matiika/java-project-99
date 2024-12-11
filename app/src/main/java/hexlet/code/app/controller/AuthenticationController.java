package hexlet.code.app.controller;

import hexlet.code.app.DTO.AuthRequest;
import hexlet.code.app.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

//    @PostMapping("/login")
//    public String create(@RequestBody AuthRequest authRequest) {
//        var authentication = new UsernamePasswordAuthenticationToken(
//                authRequest.getUsername(), authRequest.getPassword());
//
//        authenticationManager.authenticate(authentication);
//
//        var token = jwtUtils.generateToken(authRequest.getUsername());
//        return token;
//    }

    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        try {
            var authentication = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword());

            authenticationManager.authenticate(authentication);

            var token = jwtUtils.generateToken(authRequest.getUsername());
            return token;
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }
    }
}
