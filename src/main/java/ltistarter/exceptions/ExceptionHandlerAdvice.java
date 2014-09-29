/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.exceptions;

import java.util.ArrayList;
import java.util.List;

import javassist.NotFoundException;

import javax.servlet.http.HttpServletRequest;

import ltistarter.model.error.ErrorInfo;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Class that handles ltistarter.exceptions, returning proper HTTP codes and useful messages.
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionHandlerAdvice {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    public ExceptionHandlerAdvice() {
    }

    @ExceptionHandler(NotImplementedException.class)
    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
    @ResponseBody
    public ErrorInfo handleNotImplementedException(final HttpServletRequest request, final NotImplementedException e) {
        final ErrorInfo result = new ErrorInfo(HttpStatus.NOT_IMPLEMENTED, request, e.getLocalizedMessage());
        this.logException(e);
        this.logError(result);
        return result;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo handleNotImplementedException(final HttpServletRequest request, final EntityNotFoundException e) {
        final ErrorInfo result = new ErrorInfo(HttpStatus.NOT_FOUND, request, e.getLocalizedMessage());
        this.logException(e);
        this.logError(result);
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo handleMethodArgumentNotValidException(final HttpServletRequest request, MethodArgumentNotValidException e) {
        final List<String> errorMessages = new ArrayList<String>();
        for (ObjectError oe : e.getBindingResult().getAllErrors()) {
            if (oe instanceof FieldError) {
                final FieldError fe = (FieldError)oe;
                final String msg = String.format(
                        "Field error in object '%s' on field '%s': rejected value [%s].", fe.getObjectName(), fe.getField(), fe.getRejectedValue());
                errorMessages.add(msg);
            } else {
                errorMessages.add(oe.toString());
            }
        }
        final ErrorInfo result = new ErrorInfo(HttpStatus.BAD_REQUEST, request, errorMessages);
        this.logException(e);
        this.logError(result);
        return result;
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo handleUnrecognizedPropertyException(final HttpServletRequest request, UnrecognizedPropertyException e) {
        final String errorMessage = String.format("Unrecognized property: [%s].", e.getPropertyName());
        final ErrorInfo result = new ErrorInfo(HttpStatus.BAD_REQUEST, request, errorMessage);
        this.logException(e);
        this.logError(result);
        return result;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorInfo handleHttpMessageNotReadableException(final HttpServletRequest request, HttpMessageNotReadableException e) {
        if (e.getCause() instanceof UnrecognizedPropertyException) {
            return this.handleUnrecognizedPropertyException(request, (UnrecognizedPropertyException)e.getCause());
        } else {
            ErrorInfo result;
            if (e.getCause() instanceof JsonProcessingException) {
                final JsonProcessingException jpe = (JsonProcessingException)e.getCause();
                result = new ErrorInfo(HttpStatus.BAD_REQUEST, request, jpe.getOriginalMessage());
            } else {
                result = new ErrorInfo(HttpStatus.BAD_REQUEST, request, e);
            }
            this.logException(e);
            this.logError(result);
            return result;
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo exception(final HttpServletRequest request, Exception e) throws Exception {
        final String logMessageReferenceId = RandomStringUtils.randomAlphanumeric(8);
        final ErrorInfo result = new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, request, "Unexpected error [reference ID: " + logMessageReferenceId + "].");
        logger.debug("Unexpected exception [refId: {}]: {}", logMessageReferenceId, e);
        this.logError(result);
        return result;
    }

    private void logException(final Exception e) {
        logger.debug("Exception message: {}", e.getMessage());
    }

    private void logError(final ErrorInfo error) {
        logger.debug("Returning error: {}", error);
    }

}
