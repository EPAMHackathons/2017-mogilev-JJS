package com.jjsbot;

import com.jjsbot.services.PersonService;
import com.jjsbot.vkApiHandlers.CallbackApiHandler;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.Properties;


@SpringBootApplication
public class JjsbotApplication {
    private final static String PROPERTIES_FILE = "config.properties";

    public static void main(String[] args) throws IOException, ClientException, ApiException {
        SpringApplication.run(JjsbotApplication.class, args);
        PersonService service = new PersonService();
        service.getPersonById(48416512,"7467b2671b6982637ac57284d7b0fed054ee68663cbe5eaddd2190a07e957c733c32d5f60241cc1474a5e",2);
        CallbackApiHandler callbackApiHandler = new CallbackApiHandler();
    }

    private static GroupActor initVkApi(VkApiClient apiClient, Properties properties) {
        int groupId = Integer.parseInt(CallbackApiHandler.groupId);
        String token = CallbackApiHandler.token;
        if (groupId == 0 || token == null) throw new RuntimeException("Params are not set");

        UserActor actor = new UserActor(groupId, token);
        GroupActor groupActor = new GroupActor(groupId, token);
        try {
            apiClient.groups().setCallbackSettings(actor, groupActor.getGroupId()).messageNew(true).execute();
        } catch (ApiException e) {
            throw new RuntimeException("Api error during init", e);
        } catch (ClientException e) {
            throw new RuntimeException("Client error during init", e);
        }

        return groupActor;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                        .allowedHeaders("header1", "header2") //What is this for?
                        .allowCredentials(true);
            }
        };
    }
    @Bean
    public VkApiClient vkApiClient(){
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient);
    }
}

