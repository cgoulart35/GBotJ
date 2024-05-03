package com.stormerg.gbotj.services.discord.commands;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public abstract class AbstractCommandModule implements CommandModule {

    protected final PropertiesManager propertiesManager;
    protected final LoggingService loggingService;
    protected final FirebaseService firebaseService;

    protected Logger LOGGER;

    protected SlashCommandData[] supportedCommands;

    public AbstractCommandModule(final PropertiesManager propertiesManager,
                                 final LoggingService loggingService,
                                 final FirebaseService firebaseService) {
        this.propertiesManager = propertiesManager;
        this.loggingService = loggingService;
        this.firebaseService = firebaseService;

        this.setLogger();
        this.setSupportedCommands();
    }

    protected abstract void setLogger();
    protected abstract void setSupportedCommands();
    public abstract Mono<Void> handleCommand(final SlashCommandData command, final SlashCommandInteractionEvent event);

    public SlashCommandData[] getSupportedCommands() {
        return supportedCommands;
    }

    protected void sendMessage(final SlashCommandInteractionEvent event,
                               final boolean isAcknowledge,
                               final String message) {
        MessageChannelUnion channel = event.getChannel();

        String guildMsg = "";
        if (channel.getType() != ChannelType.PRIVATE && channel.getType() != ChannelType.UNKNOWN) {
            Guild guild = ((GuildChannel) channel).getGuild();
            guildMsg = " in guild " + guild.getId() + "/" + guild.getName();
        }

        LOGGER.info("Sending message to {} channel {}/{}{}: {}",
                channel.getType().name(), channel.getId(), channel.getName(), guildMsg, message);

        if (isAcknowledge)
            event.reply(message).queue();
        else
            channel.sendMessage(message).queue();
    }
}
