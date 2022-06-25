package org.cascadebot.cascadebot.utils.votes;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.function.BiConsumer;

public enum VoteFinishFunction {
    DUMMY(((textChannel, voteResults) -> {

    }));

    private final BiConsumer<TextChannel, List<VoteResult>> consumer;
    VoteFinishFunction(BiConsumer<TextChannel, List<VoteResult>> consumer) {
        this.consumer = consumer;
    }

    public BiConsumer<TextChannel, List<VoteResult>> getConsumer() {
        return consumer;
    }
}