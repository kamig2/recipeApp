package pl.kamilagronska.recipes_app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    Long id;
    int rate;
    String opinion;
}
