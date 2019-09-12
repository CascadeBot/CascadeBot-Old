package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.graphql.objects.SettingsWrapper;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.GuildSettingsCore;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.utils.ReflectionUtils;
import org.cascadebot.cascadebot.utils.SettingsUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingsService {

    @Getter
    private static SettingsService instance = new SettingsService();

    @GraphQLQuery
    public List<SettingsWrapper> settingsInformation() {
        return SettingsUtils.getAllSettings()
                .values()
                .stream()
                .map(field -> SettingsWrapper.from(field, field.getAnnotation(Setting.class)))
                .collect(Collectors.toList());
    }

    @GraphQLMutation
    public GuildSettingsCore updateCoreSettings(@GraphQLRootContext QLContext context, long guildId, @GraphQLNonNull Map<String, Object> newSettings) {
        return context.runIfAuthenticatedGuild(guildId, (guild, member) -> {
            try {
                GuildSettingsCore newCoreSettings = ReflectionUtils.assignMapToObject(context.getGuildData(guildId).getCoreSettings(), newSettings, true);
                context.getGuildData(guildId).setCoreSettings(newCoreSettings);
                return newCoreSettings;
            } catch (IllegalAccessException e) {
                // Rethrow this to be shown to
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                return null;
            }
        });
    }

}
