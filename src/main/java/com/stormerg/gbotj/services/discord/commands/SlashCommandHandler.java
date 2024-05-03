package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import reactor.core.publisher.Mono;

public interface SlashCommandHandler {

    SlashCommandData[] getSupportedSlashCommands();
    Mono<Void> handleSlashCommand(SlashCommandInteractionEvent event);

}
