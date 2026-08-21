package joseph.com.authifyy.configurations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import joseph.com.authifyy.entities.UserEntityWrapper;
import joseph.com.authifyy.services.AuthService;
import joseph.com.authifyy.services.JWTService;
import joseph.com.authifyy.services.RedisService;
import lombok.AllArgsConstructor;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final JWTService jwtService;
    private final RedisService redisService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserEntityWrapper userEntityWrapper = (UserEntityWrapper) authentication.getPrincipal();
        String regToken = "";

        String jwtTokenForOauth2 = jwtService.generateToken(userEntityWrapper);
        ResponseCookie responseCookie = ResponseCookie.from("Auth-Token", jwtTokenForOauth2)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(jwtService.getExpiration2()/1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        getRedirectStrategy().sendRedirect(request, response, "https://teachassist-delta.vercel.app/");

    }
}
