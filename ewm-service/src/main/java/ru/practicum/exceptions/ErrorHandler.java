package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        logError(e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflictHandler(final ConflictException e) {
        logError(e);
        HttpStatus status = HttpStatus.CONFLICT;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        logError(e);
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        logError(e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestParameter(final MissingServletRequestParameterException e) {
        logError(e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        logError(e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ErrorResponse.builder(status.value(), status.getReasonPhrase())
                .message(e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();
    }


    private void logError(Exception e) {
        String template = """
                \n================================================= ERROR ==================================================
                Message: {}
                Exception type: {}
                """;

        log.error(template, e.getMessage(), e.getClass().getName(), e);
    }

    private String getStackTrace(final Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}