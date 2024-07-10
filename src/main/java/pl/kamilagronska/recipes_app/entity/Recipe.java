package pl.kamilagronska.recipes_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity(name = "Recipe")
public class Recipe {
    @Id
    @Column(name = "recipe_id",updatable = false)
    private Long recipeId;
    @Column(name ="title", nullable = false, columnDefinition = "TEXT")
    private String title;
    @Column(name="description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "user_id",nullable = false,updatable = false)
    private long userId;
    @Column(name = "status", nullable = false)
    private boolean status;
    @Column(name = "note")
    private float note;
    @Column(name = "date")
    private LocalDate date;

    public Recipe(String title, String description, long userId, boolean status, float note,LocalDate date) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.status = status;
        this.note = note;
        this.date = date;
    }

    public Recipe() {

    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public float getNote() {
        return note;
    }

    public void setNote(float note) {
        this.note = note;
    }
}
