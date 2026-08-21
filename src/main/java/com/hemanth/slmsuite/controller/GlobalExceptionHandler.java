package com.hemanth.slmsuite.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("errorTitle", "Cannot Complete This Action");
        model.addAttribute("errorMessage",
                "This record cannot be deleted because it is referenced by other records (such as warranty claims or orders). Remove those references first.");
        return "error-page";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleGenericRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("errorTitle", "Something Went Wrong");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-page";
    }
}