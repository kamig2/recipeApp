package pl.kamilagronska.recipes_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "Recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @Column(name = "recipe_id",updatable = false)
    private Long recipeId;
    @Column(name ="title", nullable = false)
    private String title;
    @Column(name="description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "note")
    private float note;
    @Column(name = "date")
    private LocalDate date;

    public Recipe(String title, String description, User user, Status status, float note,LocalDate date) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.status = status;
        this.note = note;
        this.date = date;
    }

}
