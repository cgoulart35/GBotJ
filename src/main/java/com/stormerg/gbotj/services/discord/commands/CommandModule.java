package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface CommandModule {

    SlashCommandData[] getSupportedCommands();
    void handleCommand(SlashCommandData command, SlashCommandInteractionEvent event);

}
