package hexlet.code.app.handler;

import hexlet.code.app.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("error", "Access Denied");
//        response.put("code", HttpStatus.FORBIDDEN.value());
//        response.put("message", "You do not have permission to perform this action.");
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }
}
