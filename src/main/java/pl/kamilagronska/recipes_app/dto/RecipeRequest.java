package pl.kamilagronska.recipes_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import pl.kamilagronska.recipes_app.entity.Status;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {
    String title;
    String ingredients;
    String description;
    Status status;
    List<MultipartFile> files;
}

