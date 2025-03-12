package application.wrapper;

import application.model.GeneralResponse;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class ResponseWrapper {

    public static Mono<GeneralResponse> prepareSuccessResponse(Object body) {
        return Mono.just(new GeneralResponse(HttpStatus.OK.toString(), body));
    }

    public static Mono<GeneralResponse> prepareErrorResponse(HttpStatus status, String message) {
        return Mono.just(new GeneralResponse(status.toString(), message));
    }
}
