package com.jjsbot.services;

import com.jjsbot.models.Person;

interface PersonService {
    Person getPersonById(final String id);
}
