package application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubApiException extends RuntimeException {
    private final HttpStatus status;

    public GithubApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}