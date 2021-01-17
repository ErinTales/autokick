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
            description = "Blacklisted Names. Format: '[name], [name], ...' Replace -s and _s with spaces.",
            position = 1
    )
    default String blacklist() { return ""; }

    @ConfigItem( //Requires modification of FriendsChatSendKick.rs2asm
            keyName = "suppressKick",
            name = "Suppress Kick Messages",
            description = "Suppress the kick messages for users who you kicked via this plugin.",
            position = 2
    )
    default boolean suppressKick() { return false; }

    @ConfigItem(
            keyName = "kickCommand",
            name = "Enable kick command",
            description = "Enables the ::kick [username] command.",
            position = 3
    )
    default boolean kickCommand() { return false; }

    @ConfigItem(
            keyName = "kickAllCommand",
            name = "Enable kick all command",
            description = "Enables the ::kickall command, to kick everyone on the blacklist. " +
                    "Please be warned that this command will generate a lot of kick messages, " +
                    "so it is recommended that Suppress Kick Messages is enabled when using this " +
                    "command. In addition, it will still generate a 'Your request to kick/ban user...' " +
                    "message for each person kicked.",
            position = 4
    )
    default boolean kickAllCommand() { return false; }

    @ConfigItem(
            keyName = "messageColor",
            name = "Warning Message Color",
            description = "Color for the message that displays who you kicked.",
            position = 5
    )
    default Color messageColor() { return Color.MAGENTA; }

}
