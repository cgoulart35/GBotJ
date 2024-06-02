package com.stormerg.gbotj.services.discord.impl.models;

import com.google.gson.Gson;
import com.stormerg.gbotj.utils.StringUtility;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Setter
@Getter
public class CommandLog {

    private final String commandName;
    private final String authorName;
    private final String serverName;
    private final String channelName;
    private final String channelType;
    private final String authorId;
    private final String serverId;
    private final String channelId;

    public CommandLog(final CustomSlashCommandData command, final SlashCommandInteractionEvent event) {
        this.commandName = command.getName();
        this.authorName = event.getUser().getName();
        this.serverName = event.getGuild() == null ? null : event.getGuild().getName();
        this.channelName = event.getChannel().getName();
        this.channelType = event.getChannel().getType().name();
        this.authorId = event.getUser().getId();
        this.serverId = event.getGuild() == null ? null : event.getGuild().getId();
        this.channelId = event.getChannel().getId();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return StringUtility.escapeForJson(gson.toJson(this));
    }
}
