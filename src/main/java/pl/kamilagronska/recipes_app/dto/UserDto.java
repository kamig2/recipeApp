package pl.kamilagronska.recipes_app.dto;

public record UserDto(
    Long userID,
    String firstName,
    String lastName,
    String userName

) {
}
