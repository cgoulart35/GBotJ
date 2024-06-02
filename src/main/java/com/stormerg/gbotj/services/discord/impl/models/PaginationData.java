package com.stormerg.gbotj.services.discord.impl.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stormerg.gbotj.utils.StringUtility;
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
    private final transient List<MessageEmbed> pages;
    private final long timeout;
    private final TimeUnit unit;

    private int currentPageIndex;
    private transient ScheduledFuture<?> deletionTask;

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

    public String getPageAsString(final MessageEmbed page) {
        Gson gson = new Gson();
        JsonObject pageObj = getPageAsJsonObject(page);
        return StringUtility.escapeForJson(gson.toJson(pageObj));
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("timeout", timeout);
        jsonObject.addProperty("unit", unit.name());
        jsonObject.addProperty("currentPageIndex", currentPageIndex);

        JsonArray pagesArray = new JsonArray();
        for (MessageEmbed page : pages) {
            JsonObject pageObj = getPageAsJsonObject(page);
            pagesArray.add(pageObj);
        }
        jsonObject.add("pages", pagesArray);

        return StringUtility.escapeForJson(gson.toJson(jsonObject));
    }

    private JsonObject getPageAsJsonObject(final MessageEmbed page) {
        JsonObject pageObj = new JsonObject();
        pageObj.addProperty("title", page.getTitle());
        pageObj.addProperty("description", page.getDescription());
        return pageObj;
    }
}
