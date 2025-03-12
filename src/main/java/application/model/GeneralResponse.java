package application.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeneralResponse(
        String status,
        Object object,
        String message
) {
    public GeneralResponse(String status, String message) {
        this(status, null, message);
    }

    public GeneralResponse(String status, Object object) {
        this(status, object, null);
    }
}
