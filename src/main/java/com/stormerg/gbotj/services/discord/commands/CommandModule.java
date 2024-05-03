package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import reactor.core.publisher.Mono;

public interface CommandModule {

    Mono<Void> handleCommand(final SlashCommandData command, final SlashCommandInteractionEvent event);
    SlashCommandData[] getSupportedCommands();

}
