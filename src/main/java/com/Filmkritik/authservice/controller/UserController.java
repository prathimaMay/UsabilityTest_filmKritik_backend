package com.Filmkritik.authservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin()
public class UserController {
	
	@PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String getUsers() {
        return "Welcome!";
    }
}
