package joseph.com.authifyy.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDto {
    private int stars;
    private String review;
    // ONLY NEED ONE DTO (no response dto is needed for this response)
}
