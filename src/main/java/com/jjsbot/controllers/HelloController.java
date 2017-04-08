package com.jjsbot.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Admin on 02.04.2017.
 */
@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @RequestMapping(method = RequestMethod.GET)
    public String hello() {
        return "7de442fd";
    }

    @RequestMapping(value = "/ab", method = RequestMethod.POST)
    public String activateBot() {
        return "587d5fe4";
    }
}
