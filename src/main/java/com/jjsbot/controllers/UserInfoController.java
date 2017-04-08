package com.jjsbot.controllers;

import com.jjsbot.models.Person;
import com.jjsbot.services.PersonService;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Admin on 08.04.2017.
 */
@RestController
@RequestMapping("/api/v1/result")
public class UserInfoController {
    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> hello() {
        Person p = new Person();
        p.setFirstName("Kostya");
        p.setLastName("Gavno");
        p.setBirthDay("yesno");
        p.setCity("Mogilev");
        p.setFriends(null);
        p.setSex(true);
        return ResponseEntity.ok(p);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getUserById(@RequestAttribute String id,
                                              @RequestAttribute String access_token) {
        try {
            return ResponseEntity.ok(personService.getPersonById(Integer.parseInt(id), access_token,2));
        } catch (ClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
