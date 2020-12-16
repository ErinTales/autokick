package net.runelite.client.plugins.autokick;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.FriendsChatMemberJoined;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.awt.*;
import java.util.List;


@PluginDescriptor(
        name = "Auto Kick",
        description = "Friends Chat blacklist plugin. Add names to the list to auto-kick them on join.",
        tags = {"players"}
)
public class AutoKickPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private AutoKickConfig config;

    private List<String> blacklistedNames;

    private boolean suppressKickMessage = false;
    private boolean suppressingNextKick = false;


    @Provides //I always forget this...
    AutoKickConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AutoKickConfig.class);
    }

    @Override
    protected void startUp()
    {
        blacklistedNames = Text.fromCSV(config.blacklist().toLowerCase());
        suppressKickMessage = config.suppressBlacklistKick();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        //Both of these may be redundant.
        blacklistedNames = Text.fromCSV(config.blacklist().toLowerCase());
        suppressKickMessage = config.suppressBlacklistKick();
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
    {
        if(blacklistedNames.contains(Text.toJagexName(event.getMember().getName()).toLowerCase()))
        {
            //Delete.
            String nameBlacklist = Text.standardize(event.getMember().getName());
            client.addChatMessage(ChatMessageType.ENGINE, "",
                    getColorTag(config.messageColor()) + "Attempting to kick " + nameBlacklist + " from friends chat...", "");

            if(suppressKickMessage)
            {
                suppressingNextKick = true;
            }
            client.runScript(ScriptID.FRIENDS_CHAT_SEND_KICK, nameBlacklist);
        }
    }

    private String getColorTag(Color color) //This is probably idiot code. Not sure if it's me or Jagex that's the idiot though.
    {
        String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return "<col=" + hex + ">";
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
    {
        if(scriptCallbackEvent.getEventName().equals("sendKickName"))
        {

            final String[] stringStack = client.getStringStack();
            final int stringSize = client.getStringStackSize();

            //This *WILL NOT WORK* without modification to the file FriendsChatSendKick.rs2asm
            if(suppressingNextKick)
            {
                //Deletes the message entirely so it won't be displayed.
                stringStack[stringSize - 2] = "";
            }

            suppressingNextKick = false;
        }
    }
}
