package com.jjsbot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsbot.models.JsonDTO;
import com.jjsbot.models.Link;
import com.jjsbot.models.Person;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private VkApiClient vkApiClient = new VkApiClient(HttpTransportClient.getInstance());

    public Person getPersonById(final int id, final String access_token, int level) throws ClientException, ApiException, InterruptedException {
        final UserActor actor = new UserActor(id, access_token);
        Person person = getPersonById(actor, id, level);
        return person;
    }

    private Person getPersonById(UserActor actor, int id, int level) throws ClientException, ApiException, InterruptedException {
        try {
            UserXtrCounters userInfo = vkApiClient.users().get(actor)
                    .fields(UserField.BDATE, UserField.CITY, UserField.PHOTO_100
                            , UserField.SEX, UserField.LISTS)
                    .userIds(String.valueOf(id)).execute().get(0);
            Thread.sleep(200);
            List<Integer> list = vkApiClient.friends().get(actor).userId(id).execute().getItems();
            List<Person> friends = new ArrayList<>();
            for (Integer value : list) {
                /* if (friends.size() > 20) {
                    break;
                } */
                if (level != 0) {
                    Person p = getPersonById(actor, value, level - 1);
                    if (p.getVkId() != 0) {
                        friends.add(getPersonById(actor, value, level - 1));
                    }
                }
            }
            String firstName = userInfo.getFirstName();
            String lastName = userInfo.getLastName();
            String image = userInfo.getPhoto100();
            String birthday = userInfo.getBdate();
            String city = userInfo.getCity().getTitle();
            Boolean sex = userInfo.getSex().getValue() == 1 ? false : true;
            final Person person = new Person();
            person.setVkId(id);
            person.setFriends(friends);
            person.setFirstName(firstName);
            person.setUserName(firstName + " " + lastName);
            person.setSex(sex);
            person.setBirthDay(birthday);
            person.setImage(image);
            person.setLastName(lastName);
            person.setCity(city);
            return person;
        } catch (Exception e){
            return new Person();
        }
    }

    private List<Link> getLinksList(Person person) {
        List<Link> links = new ArrayList<>();
        for (Person friend : person.getFriends()) {
            Link link = new Link();
            if (friend != null && person!= null) {
                link.setSource(friend.getVkId());
                link.setTarget(person.getVkId());
            }
            links.add(link);
            if (friend.getFriends() != null && !friend.getFriends().isEmpty()) {
                links.addAll(getLinksList(friend));
            }
        }
        return links.stream().distinct().collect(Collectors.toList());
    }

    private List<Person> getPersonsList(Person person) {
        List<Person> persons = new ArrayList();
        for (Person friend : person.getFriends()) {
            persons.add(friend);
            if (friend.getFriends() != null && !friend.getFriends().isEmpty()) {
                persons.addAll(getPersonsList(friend));
            }
        }
        return persons.stream().distinct().collect(Collectors.toList());
    }

    public String arrangeToOlegJson(final Person person) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List links = getLinksList(person);
        List persons = getPersonsList(person);
        JsonDTO jsonDTO = new JsonDTO(persons, links);
        return mapper.writeValueAsString(jsonDTO);
    }
}
