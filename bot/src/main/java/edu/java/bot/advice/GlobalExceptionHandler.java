package edu.java.bot.advice;

import edu.java.bot.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_REQUEST_PARAMETERS = "Invalid request parameters";
    private static final String SOMETHING_WENT_WRONG = "Something went wrong";

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(Exception exception) {
        return getApiErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, SOMETHING_WENT_WRONG);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return getApiErrorResponse(exception, HttpStatus.BAD_REQUEST, INVALID_REQUEST_PARAMETERS);
    }

    private String[] getStacktrace(Exception exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        String[] stacktrace = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            stacktrace[i] = stackTraceElements[i].toString();
        }
        return stacktrace;
    }

    private ApiErrorResponse getApiErrorResponse(Exception exception, HttpStatus status, String description) {
        String code = status.toString();
        String exceptionName = exception.getClass().getName();
        String exceptionMessage = exception.getMessage();
        String[] stacktrace = getStacktrace(exception);

        return new ApiErrorResponse(description, code, exceptionName, exceptionMessage, stacktrace);
    }
}
