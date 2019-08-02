package org.cascadebot.cascadebot.data.graphql.services;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.graphql.objects.QLContext;
import org.cascadebot.cascadebot.data.graphql.objects.SettingsWrapper;
import org.cascadebot.cascadebot.data.objects.GuildData;
import org.cascadebot.cascadebot.data.objects.Setting;
import org.cascadebot.cascadebot.utils.SettingsUtils;

import java.lang.reflect.Field;
import java.util.List;
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

    @GraphQLQuery
    public SettingsWrapper setting(@GraphQLRootContext QLContext context, String name) {
        return context.runIfAuthenticated(QLContext.AuthenticationLevel.GUILD, () -> {
            Field setting = SettingsUtils.getAllSettings().get(name);
            if (setting == null) return null;

            GuildData guildData = context.getGuildData();
            if (guildData == null) return null;

            // TODO We need to find a way to get the settings class instance
            return null;
        });
    }





}
