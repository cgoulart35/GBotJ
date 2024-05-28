package com.stormerg.gbotj.services.discord.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface PaginationService {

    void onMessageReactionAdd(MessageReactionAddEvent event);
    void paginate(final SlashCommandInteractionEvent event,
                  final boolean isAcknowledge,
                  final long timeout,
                  final TimeUnit unit,
                  final List<MessageEmbed> pages);
}
