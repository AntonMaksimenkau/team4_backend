package com.team4.testingsystem.exceptions;

import com.team4.testingsystem.dto.ErrorResponse;
import com.team4.testingsystem.dto.TestsLimitExceededResponse;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.AccessControlException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConflictException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {FileOperationException.class})
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    public ErrorResponse handleFileOperationException(FileOperationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {IncorrectCredentialsException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotFoundException(IncorrectCredentialsException e) {
        return new ErrorResponse("Incorrect login or password");
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {TestsLimitExceededException.class})
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public TestsLimitExceededResponse handleTestsLimitExceededException(TestsLimitExceededException e) {
        return new TestsLimitExceededResponse(e.getMessage());
    }

    @ExceptionHandler(value = {PSQLException.class})
    protected ResponseEntity<Object> handlePostgreSQLException(PSQLException e, WebRequest request) {
        String body = "Incorrect data provided!";

        final ServerErrorMessage errorMessage = e.getServerErrorMessage();
        if (errorMessage != null) {
            body = "Incorrect data provided for column '" + errorMessage.getColumn() + "'";
        }
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {AccessControlException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponse handleDoNotHaveRightsException(AccessControlException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {IllegalGradeException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalGradeException(IllegalGradeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {AnswersAreBadException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAnswersAreBadException(AnswersAreBadException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {IllegalDeadlineException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalDeadlineException(IllegalDeadlineException e) {
        return new ErrorResponse(e.getMessage());
    }
}
