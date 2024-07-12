package pl.kamilagronska.recipes_app.request;

public record UserLoginRequest(
        String userName,
        String password
) {
}
