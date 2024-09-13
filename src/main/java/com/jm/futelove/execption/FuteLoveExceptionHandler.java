package com.jm.futelove.execption;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.flywaydb.core.internal.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class FuteLoveExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        }

        ProblemType problemType = ProblemType.INVALID_BODY;
        String messageUser = this.messageSource.getMessage(problemType.getMessageSource(), null,
                LocaleContextHolder.getLocale());
        Problem problem = createProblemBuild(HttpStatus.BAD_REQUEST, problemType.getUri(),
                problemType.getTitle(), messageUser).build();
        return handleExceptionInternal(ex, problem, headers, HttpStatus.BAD_REQUEST, request);
    }


    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException rootCause, HttpHeaders headers,
                                                                HttpStatusCode status, WebRequest request) {
        String path = rootCause.getPath().stream()
                .map(ref -> ref.getFieldName())
                .collect(Collectors.joining("."));

        ProblemType problemType = ProblemType.INVALID_BODY_PARAM;
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(),
                new Object[]{path, rootCause.getValue(), rootCause.getTargetType().getSimpleName()}, LocaleContextHolder.getLocale());
        Problem problem = createProblemBuild(status, problemType.getUri(), problemType.getTitle(), messageDetails)
                .build();
        return handleExceptionInternal(rootCause, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemType problemType = ProblemType.DATE_INVALED;
        List<Problem.Field> problemFields = ex.getBindingResult()
                .getFieldErrors().stream().map(fieldError -> Problem.Field.builder().name(fieldError.getField())
                        .userMessage(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(),
                new Object[]{""}, LocaleContextHolder.getLocale());
        Problem problem = createProblemBuild(status, problemType.getUri(), problemType.getTitle(), messageDetails)
                .fields(problemFields)
                .build();
        return handleExceptionInternal(ex, problem, headers, status, request);
    }


    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ProblemType problemType = ProblemType.INVALID_VALUE_LONG_DATABASE;
        String messageUser = this.messageSource.getMessage(problemType.getMessageSource(), null,
                LocaleContextHolder.getLocale());
        Problem problem = createProblemBuild(HttpStatus.BAD_REQUEST, problemType.getUri(),
                problemType.getTitle(), messageUser).build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private Problem.ProblemBuilder createProblemBuild(HttpStatusCode status, String type, String title, String detail) {
        return Problem.builder()
                .status(status.value())
                .type(type)
                .title(title)
                .details(detail);
    }
}
