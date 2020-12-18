package net.runelite.client.plugins.autokick;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("clanchat")
public interface AutoKickConfig extends Config
{
    @ConfigItem(
            keyName = "blacklist",
            name = "Blacklisted Names",
            description = "Blacklisted Names. Format: '[name], [name], ...'",
            position = 1
    )
    default String blacklist() { return ""; }

    @ConfigItem( //Currently reenabled, but requires modification of FriendsChatSendKick.rs2asm
            keyName = "suppressKick",
            name = "Suppress Kick Messages",
            description = "Suppress the kick messages for users who you kicked via this plugin.",
            position = 2
    )
    default boolean suppressKick() { return false; }

    @ConfigItem(
            keyName = "kickCommand",
            name = "Enable kick command",
            description = "Enables the ::kick [username] command",
            position = 3
    )
    default boolean kickCommand() { return false; }

    @ConfigItem(
            keyName = "messageColor",
            name = "Warning Message Color",
            description = "Color for the message that displays who you kicked.",
            position = 4
    )
    default Color messageColor() { return Color.MAGENTA; }

}
