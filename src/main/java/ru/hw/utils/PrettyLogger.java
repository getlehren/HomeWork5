package ru.hw.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

public class PrettyLogger implements HttpLoggingInterceptor.Logger {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void log(String message) {
        String trimmedMessage = message.trim();
        if ((trimmedMessage.startsWith("{") && trimmedMessage.endsWith("}"))
                || (trimmedMessage.startsWith("[") && trimmedMessage.endsWith("]"))) {
            try {
                Object value = mapper.readValue(message, Object.class);
                String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
                Platform.get().log(prettyJson, Platform.INFO, null);
            } catch (JsonProcessingException e) {
                Platform.get().log(message, Platform.WARN, e);
            }
        } else {
            Platform.get().log(message, Platform.INFO, null);
        }
    }
}

