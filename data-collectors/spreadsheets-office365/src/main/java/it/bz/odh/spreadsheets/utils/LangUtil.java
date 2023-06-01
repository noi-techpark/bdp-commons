// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.StationDto;

@Component
public class LangUtil {

    private LanguageDetector detector;

    @Value(value = "${suportedLanguages}")
    private String[] supportedLanguages;

    @PostConstruct
    private void initLanguageDetector() throws IOException {
        detector = new OptimaizeLangDetector().loadModels(new HashSet<>(Arrays.asList(supportedLanguages)));
        detector.setShortText(true);
    }
    /**
     * @param text
     * @return mapping of language with all recognized sentences in that language
     */
    private Map<String, String> mapTextToLanguage(String text) {
        Map<String,String> langMap = new HashMap<>();
        for (String sentence : text.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s")) {
            String lang = detector.detect(sentence).getLanguage();
            langMap.compute(lang, (k, v) -> (v == null) ? sentence : v +" "+ sentence);
        }
        return langMap;
    }

    /**
     * @param metaData map description column to different languages
     */
    public void guessLanguages(Map<String,Object> metaData) {
            Object object = metaData.get("description");
            if (object !=null) {
                Map<String, String> textMap = mapTextToLanguage(object.toString());
                metaData.put("description", !textMap.isEmpty() ? textMap : "");
            }
    }
    /**
     * handle multiple languages in multiple columns
     * @param metaData current metadata of {@link StationDto}
     * @param headerMapping mapping of column index and column name
     */
    public void mergeTranslations(Map<String, Object> metaData, Map<String, Short> headerMapping) {
        for (Map.Entry<String, Short> entry : headerMapping.entrySet()) {
            String[] split = entry.getKey().split(":");
            if (split.length<2)
                continue;
            try {
                LocaleUtils.toLocale(split[0]); // check if it's a valid locale
                Object object = metaData.get(split[1]);
                Object content = metaData.get(entry.getKey());
                if (content == null || content.toString().isEmpty())
                    continue;
                if (object instanceof Map) {
                    Map langMap = (Map) object;
                    langMap.put(split[0], content);
                }else if (object instanceof String) {
                    Map<String, String> langMap = mapTextToLanguage(object.toString());
                    langMap.put(split[0], content.toString());
                    metaData.put(split[1], langMap);
                }else if (object == null) {
                    Map<String,String> langMap = new HashMap<>();
                    langMap.put(split[0],content.toString());
                    metaData.put(split[1], langMap);
                }
                metaData.remove(entry.getKey());
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }
}
