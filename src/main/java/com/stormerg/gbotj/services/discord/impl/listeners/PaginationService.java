package com.stormerg.gbotj.services.discord.impl.listeners;

import com.stormerg.gbotj.services.discord.impl.helpers.InteractionHelper;
import com.stormerg.gbotj.services.discord.impl.models.PaginationData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
public class PaginationService extends ListenerAdapter {

    private static final Emoji FIRST_PAGE = Emoji.fromUnicode("⏮️");
    private static final Emoji LEFT_ARROW = Emoji.fromUnicode("⬅️");
    private static final Emoji RIGHT_ARROW = Emoji.fromUnicode("➡️");
    private static final Emoji LAST_PAGE = Emoji.fromUnicode("⏭️");
    private static final Emoji CROSS_MARK = Emoji.fromUnicode("❌");

    private final Map<String, PaginationData> paginationDataMap;
    private final ScheduledExecutorService scheduler;

    public PaginationService() {
        this.paginationDataMap = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void paginate(final SlashCommandInteractionEvent event,
                         final boolean isAcknowledge,
                         final long timeout,
                         final TimeUnit unit,
                         final List<MessageEmbed> pages) {
        MessageChannelUnion channel = event.getChannel();
        PaginationData paginationData = new PaginationData(event.getUser().getId(), pages, timeout, unit);
        MessageEmbed firstPage = pages.getFirst();
        InteractionHelper.logSendingMessage(channel, paginationData.getPageAsString(firstPage));

        if (isAcknowledge)
            event.replyEmbeds(firstPage).queue(interactionHook -> {
                interactionHook.retrieveOriginal().queue(message -> {
                    registerPaginationSession( message, paginationData);
                });
            });
        else {
            channel.sendMessageEmbeds(firstPage).queue(message -> {
                registerPaginationSession(message, paginationData);
            });
        }
    }

    private void registerPaginationSession(final Message message, final PaginationData paginationData) {
        paginationDataMap.put(message.getId(), paginationData);
        log.info("Pagination session {} registered: {}", message.getId(), paginationData);

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
                endPaginationSession(message.getId());
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
            message.removeReaction(event.getReaction().getEmoji(), event.getUser()).queue(removeReactionSuccess -> {
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
                    event.getChannel().deleteMessageById(messageId).queue(deleteMessageSuccess -> {
                        endPaginationSession(messageId);
                    }, failure -> {
                        log.error("Failed to delete pagination message and end session: {}", message.getId(), failure);
                    });
                    return;
                }

                MessageEmbed currentPage = paginationData.getCurrentPage();
                InteractionHelper.logEditingMessage(event.getChannel(), paginationData.getPageAsString(currentPage));
                event.getChannel().editMessageEmbedsById(messageId, currentPage).queue();

                // Reset the deletion timer
                paginationData.getDeletionTask().cancel(false);
                scheduleDeletion(paginationData, message);
            });
        });
    }

    private void endPaginationSession(String messageId) {
        paginationDataMap.remove(messageId);
        log.info("Pagination session {} ended and message deleted.", messageId);
    }

}
