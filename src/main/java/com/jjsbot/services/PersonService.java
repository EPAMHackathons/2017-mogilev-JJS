package com.jjsbot.services;

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

@Service
public class PersonService {
    private VkApiClient vkApiClient = new VkApiClient(HttpTransportClient.getInstance());

    public Person getPersonById(final int id, final String access_token, int level) throws ClientException, ApiException {
        final UserActor actor = new UserActor(id, access_token);
        Person person = getPersonById(actor, id, level);
        return person;
    }

    private Person getPersonById(UserActor actor, int id, int level) throws ClientException, ApiException {
        UserXtrCounters userInfo = vkApiClient.users().get(actor)
                .fields(UserField.BDATE, UserField.CITY, UserField.PHOTO_100
                        , UserField.SEX, UserField.LISTS)
                .userIds(String.valueOf(id)).execute().get(0);
        List<Integer> list = vkApiClient.friends().get(actor).execute().getItems();
        List<Person> friends = new ArrayList<>();
        for (Integer value : list) {
            if (level != 0) {
                friends.add(getPersonById(actor, id, level--));
            }
        }
        String firstName = userInfo.getFirstName();
        String lastName = userInfo.getLastName();
        String image = userInfo.getPhoto100();
        String birthday = userInfo.getBdate();
        String city = userInfo.getCity().getTitle();
        Boolean sex = userInfo.getSex().getValue() == 1 ? false : true;
        final Person person = new Person();
        person.setFriends(friends);
        person.setFirstName(firstName);
        person.setSex(sex);
        person.setBirthDay(birthday);
        person.setImage(image);
        person.setLastName(lastName);
        person.setCity(city);
        return person;
    }
}
