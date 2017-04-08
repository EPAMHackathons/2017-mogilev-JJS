package com.jjsbot.vkApiHandlers;

import com.jjsbot.JjsbotApplication;
import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;

import java.util.Random;

/**
 * Created by User on 08.04.2017.
 */

public class CallbackApiHandler extends CallbackApi {

    private static String confirmationToken = "7de442fd";
    private static String token = "dwqde535t2r32rerwe121321e31e31e2";

    private final Random random = new Random();

    @Override
    public void messageNew(Integer groupId, Message message) {
        //super.messageNew(groupId, message);
        int userId = message.getUserId();
        GroupActor actor = new GroupActor(groupId, token);

        try {
            JjsbotApplication.apiClient.messages().send(actor).message("check").userId(userId).randomId(random.nextInt()).execute();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
