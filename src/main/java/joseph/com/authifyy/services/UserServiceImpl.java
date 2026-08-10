package joseph.com.authifyy.services;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import joseph.com.authifyy.dtos.UserDto;
import joseph.com.authifyy.dtos.UserResDto;
import joseph.com.authifyy.entities.*;
import joseph.com.authifyy.repositories.RoleRepository;
import joseph.com.authifyy.repositories.UserRespository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRespository userRespository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;
    private final JWTService jwtService;

    @Override
    @Transactional
    // TODO: create a registerCli that doesn't rely on redis, by primarily changing how register works by itself
    // Creating a helper function that both registerCli and register use could massively help and reduce the clutter.
    public UserResDto register(UserDto userDto, HttpServletResponse httpServletResponse) {

        if (userRespository.existsByEmail(userDto.getEmail())) {
            throw new DataIntegrityViolationException("User already exists with given email!");
        }
        // if a collision happens still, database rules will help stop it by throwing an exception.
        // Not needed for now.
        // List<String> redisKeys = saveAndGetRedisKeys(userDto);

        // create a UserEntity.
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        // create a UserEntityWrapper to issue jwtTokens.
        UserEntityWrapper userEntityWrapper = new UserEntityWrapper(user);

        if (user.getProvider() == null) user.setProvider(ProviderType.PROVIDER_LOCAL);


        RoleEntity userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Error: Role USER was not found in the database."));


        user.getRoles().add(userRole);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRespository.save(user);
//        mailService.sendEmail(user.getEmail(), redisKeys.get(1));
//        userResDto.setPromptUUID(redisKeys.get(0));
//        userResDto.setCongratulateUUID(redisKeys.get(1));

        String jwtToken = jwtService.generateToken(userEntityWrapper);
        generateCookie(jwtToken)
                .ifPresent(cookie ->
                        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString()));
        return modelMapper.map(user, UserResDto.class);
    }

    // NOT NEEDED FOR NOW!
    private List<String> saveAndGetRedisKeys(UserDto userDto) {
        String opaquePrompt = UUID.randomUUID().toString();
        String opaqueCongratulate = UUID.randomUUID().toString();

        Duration duration = Duration.ofMinutes(3);

        redisService.save(opaquePrompt, "activation:" , userDto.getEmail(), duration);
        redisService.save(opaqueCongratulate, "activationFinish:", userDto.getEmail(), duration);

        return List.of(opaquePrompt, opaqueCongratulate);
    }

    @Override
    // for PersistLogin with axios.
    public UserResDto me() {
        //Safeguard the endpoint:
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Not authenticated");
        }


        UserEntityWrapper userPrincipal = (UserEntityWrapper) authentication.getPrincipal();

        return new UserResDto(userPrincipal.getUsername(), userPrincipal.getName(), null, null);
    }

    public Optional<ResponseCookie> generateCookie(String jwt) {

        if (jwt == null || jwt.isBlank()) {
            return Optional.empty();
        }

        ResponseCookie cookie = ResponseCookie.from("Auth-Token" , jwt)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(jwtService.getExpiration2()/1000)
                .build();

        return Optional.of(cookie);
    }
}
