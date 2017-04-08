package com.jjsbot.controllers;

import com.jjsbot.models.Person;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Admin on 08.04.2017.
 */
@RestController
@RequestMapping("/api/v1/result")
public class UserInfoController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<Person> hello() {
        Person p = new Person();
        p.setFirstName("Kostya");
        p.setLastName("Gavno");
        p.setBirthDay("yesno");
        p.setCity("Mogilev");
        p.setFriends(null);
        p.setSex(true);
        return ResponseEntity.ok(p);
    }
}
