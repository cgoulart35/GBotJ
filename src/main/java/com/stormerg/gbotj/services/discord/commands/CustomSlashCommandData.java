package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface CustomSlashCommandData extends SlashCommandData {

    boolean isPrivateMessageOnly();
    void setPrivateMessageOnly(boolean privateMessageOnly);

}
