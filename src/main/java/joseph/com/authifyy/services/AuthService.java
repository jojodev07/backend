package joseph.com.authifyy.services;

import jakarta.servlet.http.HttpServletResponse;
import joseph.com.authifyy.dtos.AuthDto;
import joseph.com.authifyy.dtos.TempResAuthDto;
import joseph.com.authifyy.dtos.VerifyDto;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


public interface AuthService {
    TempResAuthDto authenticate(AuthDto authDto, HttpServletResponse httpServletResponse);
    Optional<ResponseCookie> generateCookie(String jwt);
    TempResAuthDto authenticateCli(AuthDto authDto);
}
