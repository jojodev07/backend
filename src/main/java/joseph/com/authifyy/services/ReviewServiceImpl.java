package joseph.com.authifyy.services;

import joseph.com.authifyy.dtos.ReviewDto;
import joseph.com.authifyy.entities.ReviewEntity;
import joseph.com.authifyy.repositories.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    // Gets all reviews.
    @Override
    public List<ReviewDto> getReviews() {
        return reviewRepository.findAll().stream()
                .map(entity -> new ReviewDto(entity.getStars(), entity.getReview()))
                .toList();
    }

    @Override
    public ReviewDto addReview(ReviewDto reviewDto) {
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setStars(reviewDto.getStars());
        reviewEntity.setReview(reviewDto.getReview());

        reviewRepository.save(reviewEntity);

        return reviewDto;
    }


}
