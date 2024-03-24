package edu.java.scrapper.advice;

import edu.java.scrapper.dto.response.ApiErrorResponse;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import edu.java.scrapper.exception.ReRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String SOMETHING_WENT_WRONG = "Something went wrong";
    private static final String INVALID_REQUEST_PARAMETERS = "Invalid request parameters";
    private static final String RE_REGISTRATION = "Chat re-registration";
    private static final String NOT_REGISTERED = "Chat is not registered";
    private static final String RE_ADDING_LINK = "Re-adding link";
    private static final String LINK_IS_NOT_TRACKED = "Link is not tracked";

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(Exception exception) {
        return getApiErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, SOMETHING_WENT_WRONG);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class})
    public ApiErrorResponse handleMessageException(Exception exception) {
        return getApiErrorResponse(exception, HttpStatus.BAD_REQUEST, INVALID_REQUEST_PARAMETERS);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(ReRegistrationException.class)
    public ApiErrorResponse handleReRegistrationException(ReRegistrationException exception) {
        return getApiErrorResponse(exception, HttpStatus.CONFLICT, RE_REGISTRATION);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(ChatIsNotRegisteredException.class)
    public ApiErrorResponse handleChatIsNotRegisteredException(ChatIsNotRegisteredException exception) {
        return getApiErrorResponse(exception, HttpStatus.CONFLICT, NOT_REGISTERED);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(ReAddLinkException.class)
    public ApiErrorResponse handleReAddLinkException(ReAddLinkException exception) {
        return getApiErrorResponse(exception, HttpStatus.CONFLICT, RE_ADDING_LINK);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(LinkIsNotTrackedException.class)
    public ApiErrorResponse handleLinkIsNotTrackedException(LinkIsNotTrackedException exception) {
        return getApiErrorResponse(exception, HttpStatus.CONFLICT, LINK_IS_NOT_TRACKED);
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
