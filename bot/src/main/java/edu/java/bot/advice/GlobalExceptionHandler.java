package edu.java.bot.advice;

import edu.java.bot.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DESCRIPTION = "Invalid request parameters";

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handle(HttpMessageNotReadableException exception) {
        String code = HttpStatus.BAD_REQUEST.toString();
        String exceptionName = exception.getClass().getName();
        String exceptionMessage = exception.getMessage();
        String[] stacktrace = getStacktrace(exception);

        return new ApiErrorResponse(DESCRIPTION, code, exceptionName, exceptionMessage, stacktrace);
    }

    private String[] getStacktrace(HttpMessageNotReadableException exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        String[] stacktrace = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            stacktrace[i] = stackTraceElements[i].toString();
        }
        return stacktrace;
    }
}
