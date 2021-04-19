package br.com.zupacademy.desafioproposta.compartilhado.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class APIErrorHandler {

    private OffsetDateTime timestamp;
    private int status;
    private List<ErrorField> errors;

    /**
     * @param fieldErrors lista de org.springframework.validation.FieldError. Atribui o HttpStatus para BAD_REQUEST
     * */
    public APIErrorHandler(List<FieldError> fieldErrors) {
        this(fieldErrors, HttpStatus.BAD_REQUEST);
    }

    /**
     * @param fieldErrors lista de org.springframework.validation.FieldError
     * @param httpStatus org.springframework.http.HttpStatus.
     * */
    public APIErrorHandler(List<FieldError> fieldErrors, HttpStatus httpStatus) {
        this.timestamp = OffsetDateTime.now();
        this.status = httpStatus.value();
        setErrors(fieldErrors);
    }

    private void setErrors(List<FieldError> fieldErrors) {
        this.errors = fieldErrors.stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public List<ErrorField> getErrors() {
        return errors;
    }
}

class ErrorField {

    private String field;
    private String message;

    public ErrorField(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
