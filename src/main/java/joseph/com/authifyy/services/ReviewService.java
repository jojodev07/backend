package joseph.com.authifyy.services;

import joseph.com.authifyy.dtos.ReviewDto;
import joseph.com.authifyy.entities.ReviewEntity;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getReviews();
    ReviewDto addReview(ReviewDto reviewDto);
}
