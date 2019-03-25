/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.messaging;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class NoOpWebhookClient extends WebhookClient {

    public NoOpWebhookClient() {
        super(-1, null, true, null, null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull WebhookMessage message) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull File file) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull File file, @NotNull String fileName) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull byte[] data, @NotNull String fileName) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull InputStream data, @NotNull String fileName) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull Collection<WebhookEmbed> embeds) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull String content) {
        return CompletableFuture.completedFuture(null);
    }
}
