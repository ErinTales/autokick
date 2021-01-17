package net.runelite.client.plugins.autokick;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
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
    private ClientThread clientThread;

    @Inject
    private AutoKickConfig config;

    private List<String> blacklistedNames;

    private boolean suppressKick = false;

    @Provides //I always forget this...
    AutoKickConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AutoKickConfig.class);
    }

    @Override
    protected void startUp()
    {
        blacklistedNames = Text.fromCSV(config.blacklist().toLowerCase());
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        //This may be redundant.
        blacklistedNames = Text.fromCSV(config.blacklist().toLowerCase());
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

            suppressKick = true;
            client.runScript(ScriptID.FRIENDS_CHAT_SEND_KICK, nameBlacklist);
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
    {
        if(scriptCallbackEvent.getEventName().equals("sendKickName") && config.suppressKick() && suppressKick)
        {
            //This *WILL NOT WORK* without modification to the file FriendsChatSendKick.rs2asm
            final String[] stringStack = client.getStringStack();
            final int stringSize = client.getStringStackSize();

            //Deletes the message entirely so it won't be displayed.
            stringStack[stringSize - 2] = "";

            suppressKick = false;
        }
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted)
    {
        if(commandExecuted.getCommand().equalsIgnoreCase("kickall") && config.kickAllCommand()) {
            for (String s : blacklistedNames) {

                clientThread.invokeLater(() ->
                {
                    suppressKick = true;
                    client.runScript(ScriptID.FRIENDS_CHAT_SEND_KICK, s);
                });
            }

            client.addChatMessage(ChatMessageType.ENGINE, "",
                    getColorTag(config.messageColor()) + "Attempting to kick everyone on your blacklist...", "");
        }
        else if(commandExecuted.getCommand().equalsIgnoreCase("kick") && config.kickCommand())
        {
            String[] args = commandExecuted.getArguments();
            StringBuilder name = new StringBuilder();

            for(String s : args) //names with spaces will be read as having multiple args, so we need to iterate through them.
            {
                name.append(s);
                name.append(" ");
            }
            name = new StringBuilder(name.toString().trim());

            client.addChatMessage(ChatMessageType.ENGINE, "",
                    getColorTag(config.messageColor()) + "Attempting to kick " + name + " from friends chat...", "");

            StringBuilder finalName = name;

            if(config.suppressKick()) {
                suppressKick = true;
            }

            clientThread.invokeLater(() -> client.runScript(ScriptID.FRIENDS_CHAT_SEND_KICK, finalName.toString()));
        }
    }

    private String getColorTag(Color color) //This is probably idiot code. Not sure if it's me or Jagex that's the idiot though.
    {
        String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return "<col=" + hex + ">";
    }
}
