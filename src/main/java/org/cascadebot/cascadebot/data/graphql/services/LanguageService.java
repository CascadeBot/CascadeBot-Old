package org.cascadebot.cascadebot.data.graphql.services;

import com.google.gson.JsonObject;
import io.github.binaryoverload.JSONConfig;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.data.language.Locale;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageService {

    @Getter
    private static LanguageService instance = new LanguageService();

    @GraphQLQuery
    public JsonObject language(String languageKey) {
        Locale locale = Arrays.stream(Locale.values())
                .filter(locale1 -> locale1.getLanguageCode().equalsIgnoreCase(languageKey))
                .findFirst()
                .orElse(null);

        if (locale == null) return null;
        JSONConfig languageFile = Language.getLanguage(locale);
        if (languageFile == null) return null;

        return languageFile.getObject();
    }

}
