package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.CustomSlashCommandData;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class CustomCommandDataImpl extends CommandDataImpl implements CustomSlashCommandData {

    private boolean privateMessageOnly = false;

    public CustomCommandDataImpl(@NotNull String name, @NotNull String description) {
        super(name, description);
    }
}
