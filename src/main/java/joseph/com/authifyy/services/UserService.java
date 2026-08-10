package joseph.com.authifyy.services;

import jakarta.servlet.http.HttpServletResponse;
import joseph.com.authifyy.dtos.UserDto;
import joseph.com.authifyy.dtos.UserResDto;
import joseph.com.authifyy.entities.UserEntity;
import joseph.com.authifyy.entities.UserEntityWrapper;

import java.nio.file.AccessDeniedException;

public interface UserService {

    UserResDto register(UserDto userDto, HttpServletResponse httpServletResponse);
    UserResDto me();
}
