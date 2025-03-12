package application.exception;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(String message) {
        super(message);
    }
}
