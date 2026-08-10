package joseph.com.authifyy.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TempResAuthDto {

    private final String tempMsg = "HardCoded.";
    private String email;
    private String jwtToken;
    private String promptUUID;

}
