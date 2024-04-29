package org.prof.it.soft.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method handles validation errors thrown by the application.
     * It returns a bad request response with the validation errors.
     *
     * @param ex the exception thrown by the application
     * @return a ResponseEntity with the validation errors and a bad request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Collection<String>>> handleValidationErrors(final MethodArgumentNotValidException ex) {
        Set<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toSet());
        return ResponseEntity.badRequest().body(getErrorsMap(errors));
    }

    /**
     * This method handles not found exceptions thrown by the application.
     * It returns a not found response with the error message.
     *
     * @param ex the exception thrown by the application
     * @return a ResponseEntity with the error message and a not found status
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Collection<String>>> handleNotFoundException(final NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorsMap(Collections.singleton(ex.getMessage())));
    }

    /**
     * This method creates a map with the key "errors" and the given errors as the value.
     * It is used to create the body of the response entities in the exception handler methods.
     *
     * @param errors the errors to include in the map
     * @return a map with the key "errors" and the given errors as the value
     */
    private Map<String, Collection<String>> getErrorsMap(Collection<String> errors) {
        return Collections.singletonMap("errors", errors);
    }

}