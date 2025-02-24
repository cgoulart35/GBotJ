package com.stormerg.gbotj.services.discord.impl.commands;

import com.stormerg.gbotj.services.discord.impl.models.CustomSlashCommandData;
import com.stormerg.gbotj.services.discord.impl.listeners.PaginationService;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import reactor.core.publisher.Mono;

public abstract class AbstractCommandModule {

    protected final PropertiesManager propertiesManager;
    protected final PaginationService paginationService;

    @Getter
    protected CustomSlashCommandData[] supportedCommands;
    @Getter
    protected String featureToggleName;
    @Setter
    protected JDA jda;

    public AbstractCommandModule(final PropertiesManager propertiesManager,
                                 final PaginationService paginationService) {
        this.propertiesManager = propertiesManager;
        this.paginationService = paginationService;

        this.setSupportedCommands();
        this.setFeatureToggleName();
    }

    // command modules are required to implement these methods
    protected abstract void setSupportedCommands();
    protected abstract void setFeatureToggleName();
    public abstract Mono<Void> handleCommand(final CustomSlashCommandData command, final SlashCommandInteractionEvent event);
}
