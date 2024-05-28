package com.stormerg.gbotj.services.discord.commands.models;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class PaginationData {

    private final String userId;
    private final List<MessageEmbed> pages;
    private final long timeout;
    private final TimeUnit unit;

    private int currentPageIndex;
    private ScheduledFuture<?> deletionTask;

    public PaginationData(String userId, List<MessageEmbed> pages, long timeout, TimeUnit unit) {
        this.userId = userId;
        this.pages = pages;
        this.timeout = timeout;
        this.unit = unit;
        this.currentPageIndex = 0;
    }

    public MessageEmbed getCurrentPage() {
        return pages.get(currentPageIndex);
    }

    public void nextPage() {
        if (currentPageIndex < pages.size() - 1) {
            currentPageIndex++;
        }
    }

    public void previousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
        }
    }

    public void firstPage() {
        currentPageIndex = 0;
    }

    public void lastPage() {
        currentPageIndex = pages.size() - 1;
    }
}
