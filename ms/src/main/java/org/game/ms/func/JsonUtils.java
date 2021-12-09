/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package org.game.ms.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wanggang
 */
@Slf4j
public class JsonUtils {

    private final static ObjectMapper mapper = new ObjectMapper();

    public JsonUtils() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static <T> T json2bean(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static LinkedHashMap<String, Object> getValueMap(Object object) {
        return mapper.convertValue(object, LinkedHashMap.class);
    }

    public static String bean2json(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static JsonNode bean2jsontree(Object object) {
        try {
            String jsonString = mapper.writeValueAsString(object);
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
