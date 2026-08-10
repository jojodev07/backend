package joseph.com.authifyy.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import joseph.com.authifyy.dtos.ErrorDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<? extends Record> handleSecurityExceptions(Exception ex) {
        HttpStatus status = ex instanceof AuthenticationException
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status).body(
                new ErrorDto(
                        ex.getMessage(),
                        status,
                        new ArrayList<>(List.of(ex.getClass().getSimpleName()))
                )
        );
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            MalformedJwtException.class,
            JwtException.class
    })
    public ResponseEntity<? extends Record> jwtExceptionHandler(JwtException jwtException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorDto(
                        jwtException.getMessage(),
                        HttpStatus.UNAUTHORIZED,
                        new ArrayList<>()
                )
        );
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    public ResponseEntity<? extends Record> DataIntegrityViolationExceptionHandler(DataIntegrityViolationException
                                                                                               dataIntegrityViolationException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorDto(
                        dataIntegrityViolationException.getMessage(),
                        HttpStatus.CONFLICT,
                        new ArrayList<>()
                )
        );
    }

    @ExceptionHandler({
            Exception.class
    })
    public ResponseEntity<? extends Record> debugExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto(
                        e.getClass().getName(),
                        HttpStatus.BAD_REQUEST,
                        new ArrayList<>()
                )
        );
    }

}
