package com.jjsbot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsbot.exceptions.BadFlowException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private VkApiClient vkApiClient = new VkApiClient(HttpTransportClient.getInstance());

    public Person getPersonById(final int id, final String access_token) throws ClientException, ApiException, InterruptedException {
        final UserActor actor = new UserActor(id, access_token);
        Person person = getPersonTreeWithoutRecursion(actor, id);
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
    private Person getPersonTreeWithoutRecursion(UserActor actor, int id){
        try {
            int counter=0;
            UserXtrCounters userInfo = fetchUserInfo(actor,id);
            Person person = fetchPersonData(userInfo);
            person.setId(counter);
            counter++;
            List<Person> myFriends = new ArrayList<>();
            Thread.sleep(200);
            List<Integer> list = vkApiClient.friends().get(actor).userId(id).execute().getItems();
            for(Integer value: list){
                List<Integer> friendsFriends = vkApiClient.friends().get(actor).userId(id).execute().getItems();
                UserXtrCounters userInfoFriend = fetchUserInfo(actor,value);
                Person myFriend = fetchPersonData(userInfoFriend);
                myFriend.setId(counter);
                counter++;
                List<Person> myFriendsFriends = new ArrayList<>();
                Thread.sleep(200);
                for(Integer friendsValue:friendsFriends){
                    Person myFriendsFriend = fetchPersonData(userInfoFriend);
                    myFriendsFriend.setId(counter);
                    counter++;
                    myFriendsFriends.add(myFriendsFriend);
                }
                myFriend.setFriends(myFriendsFriends);
                myFriends.add(myFriend);
            }
            person.setFriends(myFriends);
            return person;
        } catch (ApiException e) {
            throw new BadFlowException();
        } catch (ClientException e) {
            throw new BadFlowException();
        } catch (InterruptedException e) {
            throw new BadFlowException();
        }
    }
    private Person fetchPersonData(UserXtrCounters userInfo){
        String firstName = userInfo.getFirstName();
        String lastName = userInfo.getLastName();
        String image = userInfo.getPhoto100();
        String birthday = userInfo.getBdate();
        String city = userInfo.getCity().getTitle();
        Boolean sex = userInfo.getSex().getValue() == 1 ? false : true;
        final Person person = new Person();
        person.setVkId(userInfo.getId());
        person.setFirstName(firstName);
        person.setUserName(firstName + " " + lastName);
        person.setSex(sex);
        person.setBirthDay(birthday);
        person.setImage(image);
        person.setLastName(lastName);
        person.setCity(city);
        return person;
    }
    private UserXtrCounters fetchUserInfo(UserActor actor,int id) throws ClientException, ApiException {
        return vkApiClient.users().get(actor)
                .fields(UserField.BDATE, UserField.CITY, UserField.PHOTO_100
                        , UserField.SEX, UserField.LISTS)
                .userIds(String.valueOf(id)).execute().get(0);
    }
    private List<Link> getLinksList(Person person) {
        List<Link> links = new ArrayList<>();
        for (Person friend : person.getFriends()) {
            Link link = new Link();
            if (friend != null && person!= null) {
                link.setSource(person.getVkId());
                link.setTarget(friend.getVkId());
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
        persons.add(person);
        for (Person friend : person.getFriends()) {
            persons.add(friend);
            if (friend.getFriends() != null && !friend.getFriends().isEmpty()) {
                persons.addAll(getPersonsList(friend));
            }
        }
        Comparator<Person> personComparator = (person1, friend)->Integer.compare(person1.getId(),friend.getId());
        return persons.stream().distinct().sorted(personComparator).collect(Collectors.toList());
    }

    public String arrangeToOlegJson(final Person person) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List links = getLinksList(person);
        List persons = getPersonsList(person);
        JsonDTO jsonDTO = new JsonDTO(persons, links);
        return mapper.writeValueAsString(jsonDTO);
    }
}
