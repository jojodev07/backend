package joseph.com.authifyy.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResDto {
    private String email;
    private String name;
    private String promptUUID; // is currently useless.
    private String congratulateUUID; // so is this.
}
