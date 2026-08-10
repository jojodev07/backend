package joseph.com.authifyy.services;

import jakarta.servlet.http.HttpServletResponse;
import joseph.com.authifyy.dtos.AuthDto;
import joseph.com.authifyy.dtos.TempResAuthDto;
import joseph.com.authifyy.entities.ProviderType;
import joseph.com.authifyy.entities.UserEntity;
import joseph.com.authifyy.entities.UserEntityWrapper;
import joseph.com.authifyy.repositories.UserRespository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RedisService redisService;
    private final UserRespository userRespository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public TempResAuthDto authenticate(AuthDto authDto, HttpServletResponse httpServletResponse) {

        try {
            Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                    authDto.getEmail(),
                    authDto.getPassword()
            );

            Authentication authenticationResponse = authenticationManager.authenticate(
                    authentication
            );

            UserEntityWrapper userEntityWrapper = (UserEntityWrapper) authenticationResponse.getPrincipal();

            if (userEntityWrapper.getUser().getProvider().equals(ProviderType.PROVIDER_GOOGLE)) {
                throw new DataIntegrityViolationException("This email is associated with a social login provider. Please sign in using Google/Apple.");
            }
            String jwtToken = jwtService.generateToken(userEntityWrapper);
            generateCookie(jwtToken)
                    .ifPresent(cookie ->
                            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()));

            // TODO: fix this dto. jwtTokens are no longer passed in this dto for security reasons.
            return new TempResAuthDto(userEntityWrapper.getUsername(), null, null);
        } catch (DisabledException disabledException) {
            // Calling a raw userEntity here:
            // Technically orElse will never be called here, because the account always must exist
            // for it to be disabled.

            // all users will be activated from now because no redis.
            // this is practically dead code, keep it for later
            UserEntity user = userRespository.findByEmail(authDto.getEmail())
                    .orElse(null);

            if (!bCryptPasswordEncoder.matches(authDto.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid email or password");
            }

            String opaquePrompt = UUID.randomUUID().toString();
            redisService.save(opaquePrompt, "activation:" , user.getEmail(), Duration.ofMinutes(3));
            return new TempResAuthDto(user.getEmail(), null, opaquePrompt);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public TempResAuthDto authenticateCli(AuthDto authDto) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                authDto.getEmail(),
                authDto.getPassword()
        );

        Authentication authenticationResponse = authenticationManager.authenticate(
                authentication
        );

        UserEntityWrapper userEntityWrapper = (UserEntityWrapper) authenticationResponse.getPrincipal();

        if (userEntityWrapper.getUser().getProvider().equals(ProviderType.PROVIDER_GOOGLE)) {
            throw new DataIntegrityViolationException("Provider login is not supported in CLI!");
        }

        String jwtToken = jwtService.generateToken(userEntityWrapper);

        return new TempResAuthDto(userEntityWrapper.getUsername(), jwtToken, null);
    }


    @Override
    public Optional<ResponseCookie> generateCookie(String jwt) {

        if (jwt == null || jwt.isBlank()) {
            return Optional.empty();
        }

        ResponseCookie cookie = ResponseCookie.from("Auth-Token" , jwt)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(jwtService.getExpiration2())
                .build();

        return Optional.of(cookie);
    }



}
