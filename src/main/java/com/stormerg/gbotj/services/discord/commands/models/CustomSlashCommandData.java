package com.stormerg.gbotj.services.discord.commands.models;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class CustomSlashCommandData extends CommandDataImpl {

    private boolean privateMessageOnly = false;

    public CustomSlashCommandData(@NotNull String name, @NotNull String description) {
        super(name, description);
    }
}
