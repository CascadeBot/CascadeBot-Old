package org.cascadebot.cascadebot.data.graphql.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingsService {

    @Getter
    private static SettingsService instance = new SettingsService();



}
