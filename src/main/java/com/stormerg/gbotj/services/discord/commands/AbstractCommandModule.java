package com.stormerg.gbotj.services.discord.commands;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractCommandModule implements CommandModule {

    protected final PropertiesManager propertiesManager;
    protected final FirebaseService firebaseService;

    @Getter
    protected SlashCommandData[] supportedCommands;

    public AbstractCommandModule(final PropertiesManager propertiesManager,
                                 final FirebaseService firebaseService) {
        this.propertiesManager = propertiesManager;
        this.firebaseService = firebaseService;

        this.setSupportedCommands();
    }

    protected abstract void setSupportedCommands();
    public abstract Mono<Void> handleCommand(final SlashCommandData command, final SlashCommandInteractionEvent event);

    protected void sendMessage(final SlashCommandInteractionEvent event,
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
