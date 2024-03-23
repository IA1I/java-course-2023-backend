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
        String code = HttpStatus.INTERNAL_SERVER_ERROR.toString();
        String exceptionName = exception.getClass().getName();
        String exceptionMessage = exception.getMessage();
        String[] stacktrace = getStacktrace(exception);

        return new ApiErrorResponse(SOMETHING_WENT_WRONG, code, exceptionName, exceptionMessage, stacktrace);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String code = HttpStatus.BAD_REQUEST.toString();
        String exceptionName = exception.getClass().getName();
        String exceptionMessage = exception.getMessage();
        String[] stacktrace = getStacktrace(exception);

        return new ApiErrorResponse(INVALID_REQUEST_PARAMETERS, code, exceptionName, exceptionMessage, stacktrace);
    }

    private String[] getStacktrace(Exception exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        String[] stacktrace = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            stacktrace[i] = stackTraceElements[i].toString();
        }
        return stacktrace;
    }
}
