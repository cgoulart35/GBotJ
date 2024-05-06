package com.stormerg.gbotj.services.discord.commands.helpers;

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

        String guildMsg = "";
        if (channel.getType() != ChannelType.PRIVATE && channel.getType() != ChannelType.UNKNOWN) {
            Guild guild = ((GuildChannel) channel).getGuild();
            guildMsg = " in guild " + guild.getId() + "/" + guild.getName();
        }

        log.info("Sending message to {} channel {}/{}{}: {}",
                channel.getType().name(), channel.getId(), channel.getName(), guildMsg, message);

        if (isAcknowledge)
            event.reply(message).queue();
        else
            channel.sendMessage(message).queue();
    }
}
