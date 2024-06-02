package com.stormerg.gbotj.services.discord.impl.helpers;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Slf4j
public class InteractionHelper {

    public static void sendMessage(final SlashCommandInteractionEvent event,
                               final boolean isAcknowledge,
                               final String message) {
        MessageChannelUnion channel = event.getChannel();
        logSendingMessage(channel, message);
        if (isAcknowledge)
            event.reply(message).queue();
        else
            channel.sendMessage(message).queue();
    }

    public static void logSendingMessage(final MessageChannelUnion channel, final String message) {
        logMessage(channel, message, false);
    }

    public static void logEditingMessage(final MessageChannelUnion channel, final String message) {
        logMessage(channel, message, true);
    }

    private static void logMessage(final MessageChannelUnion channel, final String message, final boolean isEdit) {
        String guildMsg = "";
        if (channel.getType() != ChannelType.PRIVATE && channel.getType() != ChannelType.UNKNOWN) {
            Guild guild = ((GuildChannel) channel).getGuild();
            guildMsg = " in guild " + guild.getId() + "/" + guild.getName();
        }

        String sendingEditingStr = isEdit ? "Editing" : "Sending";
        String inToStr = isEdit ? "in" : "to";
        log.info("{} message {} {} channel {}/{}{}: {}",
                sendingEditingStr, inToStr, channel.getType().name(), channel.getId(), channel.getName(), guildMsg, message);
    }
}
