package joseph.com.authifyy.dtos;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public record ErrorDto(
        String message,
        HttpStatus httpStatus,
        ArrayList<String> additionalInfo
) {
}
