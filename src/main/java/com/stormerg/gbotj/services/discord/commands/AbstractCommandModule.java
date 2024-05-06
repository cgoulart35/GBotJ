package com.stormerg.gbotj.services.discord.commands;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class AbstractCommandModule implements CommandModule {

    protected final PropertiesManager propertiesManager;
    protected final FirebaseService firebaseService;

    @Getter
    protected CustomSlashCommandData[] supportedCommands;

    public AbstractCommandModule(final PropertiesManager propertiesManager,
                                 final FirebaseService firebaseService) {
        this.propertiesManager = propertiesManager;
        this.firebaseService = firebaseService;

        this.setSupportedCommands();
    }

    protected abstract void setSupportedCommands();
    public abstract Mono<Void> handleCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event);
}
