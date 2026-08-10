package joseph.com.authifyy.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import joseph.com.authifyy.dtos.AuthDto;
import joseph.com.authifyy.dtos.TempResAuthDto;
import joseph.com.authifyy.dtos.UserResDto;
import joseph.com.authifyy.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TempResAuthDto> userAuth(@Valid @RequestBody AuthDto authDto,
                                                   HttpServletResponse httpServletResponse) {
        TempResAuthDto tempResAuthDto = authService.authenticate(authDto, httpServletResponse);
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
        return responseBuilder.body(tempResAuthDto);
    }

    @PostMapping("/login-cli")
    public ResponseEntity<TempResAuthDto> userAuthCli(@Valid @RequestBody AuthDto authDto) {
        TempResAuthDto tempResAuthDto = authService.authenticateCli(authDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        tempResAuthDto
                );
    }

}
