package com.jjsbot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsbot.models.Person;
import com.jjsbot.models.TokenResponse;
import com.jjsbot.services.PersonService;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Admin on 02.05.2017.
 */
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    PersonService personService = new PersonService();

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView method() {
        return new ModelAndView("redirect:" + "https://oauth.vk.com/authorize?client_id=5972860&redirect_uri=http://localhost:8080/api/v1/token/get");

    }

    @RequestMapping(value="/get", method = RequestMethod.GET)
    public ResponseEntity<Person> getToken(@RequestParam(value = "code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException, ClientException, ApiException, InterruptedException {
        //return new ModelAndView("redirect:" + "https://oauth.vk.com/authorize?client_id=5972860&redirect_uri=http://localhost:8080/api/v1/token/get");
        String url = request.getRequestURL().toString() + "?" + request.getQueryString();
        if (url.contains("access_token")){
            System.out.println("YEAAAHH");
            System.out.println(response);
            return null;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(code);
            String urlToGetToken = "https://oauth.vk.com/access_token?client_id=5972860&client_secret=R3ya8Wu2XQkw8rlPN5Hi&redirect_uri=http://localhost:8080/api/v1/token/get&code="+code;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(urlToGetToken, String.class);
            TokenResponse token = mapper.readValue(result, TokenResponse.class);
            Person person = personService.getPersonById(token.getUser_id(), token.getAccess_token());
            //String json = personService.arrangeToOlegJson(person);


            return ResponseEntity.ok(person);
        }

    }
}
