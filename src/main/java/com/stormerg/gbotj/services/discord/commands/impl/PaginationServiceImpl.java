package com.stormerg.gbotj.services.discord.commands.impl;

import com.stormerg.gbotj.services.discord.commands.PaginationService;
import com.stormerg.gbotj.services.discord.commands.models.PaginationData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaginationServiceImpl extends ListenerAdapter implements PaginationService {

    private static final Emoji FIRST_PAGE = Emoji.fromUnicode("⏮️");
    private static final Emoji LEFT_ARROW = Emoji.fromUnicode("⬅️");
    private static final Emoji RIGHT_ARROW = Emoji.fromUnicode("➡️");
    private static final Emoji LAST_PAGE = Emoji.fromUnicode("⏭️");
    private static final Emoji CROSS_MARK = Emoji.fromUnicode("❌");

    private final Map<String, PaginationData> paginationDataMap;
    private final ScheduledExecutorService scheduler;

    public PaginationServiceImpl() {
        this.paginationDataMap = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void paginate(final SlashCommandInteractionEvent event,
                         final boolean isAcknowledge,
                         final long timeout,
                         final TimeUnit unit,
                         final List<MessageEmbed> pages) {

        MessageChannelUnion channel = event.getChannel();
        String guildMsg = "";
        if (channel.getType() != ChannelType.PRIVATE && channel.getType() != ChannelType.UNKNOWN) {
            Guild guild = ((GuildChannel) channel).getGuild();
            guildMsg = " in guild " + guild.getId() + "/" + guild.getName();
        }

        log.info("Creating new pagination session and sending message to {} channel {}/{}{}",
                channel.getType().name(), channel.getId(), channel.getName(), guildMsg);

        MessageEmbed firstPage = pages.getFirst();
        if (isAcknowledge)
            event.replyEmbeds(firstPage).queue(interactionHook -> {
                interactionHook.retrieveOriginal().queue(message -> {
                    registerPaginationSession(event.getUser(), message, timeout, unit, pages);
                });
            });
        else {
            channel.sendMessageEmbeds(firstPage).queue(message -> {
                registerPaginationSession(event.getUser(), message, timeout, unit, pages);
            });
        }
    }

    private void registerPaginationSession(final User user,
                                           final Message message,
                                           final long timeout,
                                           final TimeUnit unit,
                                           final List<MessageEmbed> pages) {
        PaginationData paginationData = new PaginationData(user.getId(), pages, timeout, unit);
        paginationDataMap.put(message.getId(), paginationData);

        message.addReaction(FIRST_PAGE).queue();
        message.addReaction(LEFT_ARROW).queue();
        message.addReaction(RIGHT_ARROW).queue();
        message.addReaction(LAST_PAGE).queue();
        message.addReaction(CROSS_MARK).queue();

        scheduleDeletion(paginationData, message);
    }

    private void scheduleDeletion(final PaginationData paginationData,
                                  final Message message) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            message.delete().queue(success -> {
                paginationDataMap.remove(message.getId());
                log.info("Deleted pagination message and ended session: {}", message.getId());
            }, failure -> {
                log.error("Failed to delete pagination message and end session: {}", message.getId(), failure);
            });
        }, paginationData.getTimeout(), paginationData.getUnit());
        paginationData.setDeletionTask(future);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) {
            return;
        }

        String messageId = event.getMessageId();
        PaginationData paginationData = paginationDataMap.get(messageId);
        if (paginationData == null || !paginationData.getUserId().equals(event.getUserId())) {
            return;
        }

        event.getChannel().retrieveMessageById(messageId).queue(message -> {
            message.removeReaction(event.getReaction().getEmoji(), event.getUser()).queue(success -> {
                String emoji = event.getReaction().getEmoji().getAsReactionCode();
                if (emoji.equals(FIRST_PAGE.getAsReactionCode())) {
                    paginationData.firstPage();
                } else if (emoji.equals(LEFT_ARROW.getAsReactionCode())) {
                    paginationData.previousPage();
                } else if (emoji.equals(RIGHT_ARROW.getAsReactionCode())) {
                    paginationData.nextPage();
                } else if (emoji.equals(LAST_PAGE.getAsReactionCode())) {
                    paginationData.lastPage();
                } else if (emoji.equals(CROSS_MARK.getAsReactionCode())) {
                    paginationDataMap.remove(messageId);
                    event.getChannel().deleteMessageById(messageId).queue();
                    return;
                }

                event.getChannel().editMessageEmbedsById(messageId, paginationData.getCurrentPage()).queue();

                // Reset the deletion timer
                paginationData.getDeletionTask().cancel(false);
                scheduleDeletion(paginationData, message);
            });
        });
    }

}
