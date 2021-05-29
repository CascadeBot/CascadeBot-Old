/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.events;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class EventCollector<T> implements Runnable {

    private final Guild guild;
    private final BiConsumer<Guild, List<T>> finishConsumer;
    private final long msTimeout;

    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private List<T> dataList = new ArrayList<>();

    public EventCollector(Guild guild, BiConsumer<Guild, List<T>> finishConsumer, long msTimeout) {
        this.guild = guild;
        this.finishConsumer = finishConsumer;
        this.msTimeout = msTimeout;
    }

    @Override
    public void run() {
        boolean collecting = true;
        while (collecting) {
            try {
                T data = queue.poll(msTimeout, TimeUnit.MILLISECONDS);
                if (data == null) {
                    collecting = false;
                    finishConsumer.accept(guild, dataList);
                } else {
                    dataList.add(data);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public BlockingQueue<T> getQueue() {
        return queue;
    }

}
