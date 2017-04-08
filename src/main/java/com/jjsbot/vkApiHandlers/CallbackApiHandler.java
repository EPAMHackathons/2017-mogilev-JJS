package com.jjsbot.vkApiHandlers;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;

/**
 * Created by User on 08.04.2017.
 */

public class CallbackApiHandler extends CallbackApi {
    @Override
    public void messageNew(Integer groupId, Message message) {
        super.messageNew(groupId, message);
    }


}
