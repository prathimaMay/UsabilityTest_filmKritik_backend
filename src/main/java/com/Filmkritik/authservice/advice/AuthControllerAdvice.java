/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Filmkritik.authservice.advice;

import com.Filmkritik.authservice.dto.AuthApiResponse;
import com.Filmkritik.authservice.exception.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthControllerAdvice {

    private static final Logger logger = Logger.getLogger(AuthControllerAdvice.class);

    private final MessageSource messageSource;

    @Autowired
    public AuthControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthApiResponse processValidationError(MethodArgumentNotValidException ex, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> allErrors = result.getAllErrors();
        String data = processAllErrors(allErrors).stream().collect(Collectors.joining("\n"));
        return new AuthApiResponse(false, data, ex.getClass().getName(), resolvePathFromWebRequest(request));
    }

    /**
     * Utility Method to generate localized message for a list of field errors
     *
     * @param allErrors the field errors
     * @return the list
     */
    private List<String> processAllErrors(List<ObjectError> allErrors) {
        return allErrors.stream().map(this::resolveLocalizedErrorMessage).collect(Collectors.toList());
    }

    /**
     * Resolve localized error message. Utility method to generate a localized error
     * message
     *
     * @param objectError the field error
     * @return the string
     */
    private String resolveLocalizedErrorMessage(ObjectError objectError) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String localizedErrorMessage = messageSource.getMessage(objectError, currentLocale);
        logger.info(localizedErrorMessage);
        return localizedErrorMessage;
    }

    private String resolvePathFromWebRequest(WebRequest request) {
        try {
            return ((ServletWebRequest) request).getRequest().getAttribute("javax.servlet.forward.request_uri").toString();
        } catch (Exception ex) {
            return null;
        }
    }

  
    @ExceptionHandler(value = InvalidTokenRequestException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public AuthApiResponse handleInvalidTokenException(InvalidTokenRequestException ex, WebRequest request) {
    	logger.error("InvalidTokenRequestException :- "+ ex.getMessage());
        return new AuthApiResponse(false, ex.getMessage(), ex.getClass().getName(), resolvePathFromWebRequest(request));
    }


    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public AuthApiResponse handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
    	logger.error("TokenRefreshException :- "+ ex.getMessage());
        return new AuthApiResponse(false, ex.getMessage(), ex.getClass().getName(), resolvePathFromWebRequest(request));
    }

//    @ExceptionHandler(value = UserLogoutException.class)
//    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
//    @ResponseBody
//    public AuthApiResponse handleUserLogoutException(UserLogoutException ex, WebRequest request) {
//        return new AuthApiResponse(false, ex.getMessage(), ex.getClass().getName(), resolvePathFromWebRequest(request));
//    }

}
