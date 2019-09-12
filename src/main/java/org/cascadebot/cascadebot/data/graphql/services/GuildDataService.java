package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.language.Locale;
import org.cascadebot.cascadebot.data.managers.GuildDataManager;
import org.cascadebot.cascadebot.data.objects.GuildData;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildDataService {

    @Getter
    private static GuildDataService instance = new GuildDataService();

    @GraphQLQuery
    public GuildData guild(@GraphQLRootContext QLContext context, long id) {
        return context.runIfAuthenticatedGuild(id, (guild, member) -> {
            return GuildDataManager.getGuildData(id);
        });
    }

    @GraphQLQuery
    public List<GuildData> userGuilds(@GraphQLRootContext QLContext context) {
        return context.runIfAuthenticatedUser(user -> {
            return CascadeBot.INS.getShardManager()
                    .getGuilds()
                    .stream()
                    // TODO: Add proper permissions
                    .filter(guild -> guild.getMember(user) != null && guild.getMember(user).hasPermission(Permission.ADMINISTRATOR))
                    .map(guild -> GuildDataManager.getGuildData(guild.getIdLong()))
                    .collect(Collectors.toList());
        });
    }

    @GraphQLQuery
    public long ownerId(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getOwnerIdLong();
    }

    @GraphQLQuery
    @GraphQLNonNull
    public String name(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getName();
    }

    @GraphQLQuery
    @GraphQLNonNull
    public String iconUrl(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getIconUrl();
    }

    @GraphQLQuery
    @GraphQLNonNull
    public String splashUrl(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getSplashUrl();
    }

    @GraphQLQuery
    public int memberCount(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getMembers().size();
    }

    @GraphQLQuery
    public int textChannelCount(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getTextChannels().size();
    }

    @GraphQLQuery
    public int voiceChannelCount(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getVoiceChannels().size();
    }

    @GraphQLQuery
    public int categoryCount(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getCategories().size();
    }

    @GraphQLQuery
    @GraphQLNonNull
    public OffsetDateTime creationTime(@GraphQLContext GuildData guildData) {
        return getGuildFromId(guildData.getGuildId()).getTimeCreated();
    }

    private Guild getGuildFromId(long guildId) {
        Guild guild = CascadeBot.INS.getShardManager().getGuildById(guildId);
        if (guild == null) {
            throw new IllegalStateException("We can't get that guild :(");
        }
        return guild;
    }

    @GraphQLMutation
    public GuildData setGuildLocale(@GraphQLRootContext QLContext context, long guildId, @GraphQLNonNull Locale locale) {
        return context.runIfAuthenticatedGuild(guildId, (guild, member) -> {
            GuildData guildData = context.getGuildData(guildId);
            guildData.setLocale(locale);
            return guildData;
        });
    }

}
