package org.nemogram.messenger.helpers;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;

import org.nemogram.messenger.helpers.remote.ConfigHelper;

public class PushHelper {

    private static final Gson GSON = new Gson();

    public static void processRemoteMessage(String data) {
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            return;
        }
        try {
            var message = GSON.fromJson(data, RemoteMessage.class);
            var action = message.action;
            switch (action) {
                case "set_remote_config":
                    ConfigHelper.getInstance().onLoadSuccess(message.data);
                    break;
            }
        } catch (Exception e) {
            FileLog.e("failed to do remote action", e);
        }
    }

    public static class RemoteMessage {
        @SerializedName("action")
        @Expose
        public String action;

        @SerializedName("data")
        @Expose
        public String data;
    }
}
