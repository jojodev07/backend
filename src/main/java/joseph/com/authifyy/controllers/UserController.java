package joseph.com.authifyy.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import joseph.com.authifyy.dtos.UserDto;
import joseph.com.authifyy.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto,
                                      HttpServletResponse httpServletResponse) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.register(userDto,httpServletResponse)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.me()
        );
    }
}
