package net.runelite.client.plugins.autokick;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.FriendsChatMemberJoined;
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
        blacklistedNames = Text.fromCSV(config.blacklist().toLowerCase()); //Might be redundant, idc.
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
    {
        if(blacklistedNames.contains(Text.toJagexName(event.getMember().getName()).toLowerCase()))
        {
            //Delete.
            String nameBlacklist = Text.standardize(event.getMember().getName());
            client.addChatMessage(ChatMessageType.ENGINE, "",
                    getColorTag(config.messageColor()) + "Attempting to kick " + nameBlacklist + "...", "");

            client.runScript(ScriptID.FRIENDS_CHAT_SEND_KICK, nameBlacklist);
        }
    }

    private String getColorTag(Color color) //This is probably idiot code. Not sure if it's me or Jagex that's the idiot though.
    {
        String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return "<col=" + hex + ">";
    }
}
