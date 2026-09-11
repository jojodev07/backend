package joseph.com.authifyy.controllers;

import jakarta.validation.Valid;
import joseph.com.authifyy.dtos.ReviewDto;
import joseph.com.authifyy.services.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/getreviews")
    ResponseEntity<?> getReviews() {
        return ResponseEntity.status(HttpStatus.OK).body(
                reviewService.getReviews()
        );
    }

    @PostMapping("/addreview")
    ResponseEntity<?> addReviews(@Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.status(HttpStatus.OK).body(
                reviewService.addReview(reviewDto)
        );
    }
}
