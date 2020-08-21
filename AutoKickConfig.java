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

    /*@ConfigItem( //Currently disabled due to RuneLite removing the ability to edit the kick string.
            keyName = "suppressBlacklistKick",
            name = "Suppress Blacklist Kick Messages",
            description = "Suppress the kick messages for users who you kicked via the blacklist function.",
            position = 2
    )
    default boolean suppressBlacklistKick() { return false; }*/

    @ConfigItem(
            keyName = "messageColor",
            name = "Warning Message Color",
            description = "Color for the message that displays who you kicked.",
            position = 2
    )
    default Color messageColor() { return Color.MAGENTA; }

}
