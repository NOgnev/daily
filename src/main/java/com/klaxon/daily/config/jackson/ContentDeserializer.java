package com.klaxon.daily.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.service.client.OpenAiClient;

import java.io.IOException;

public class ContentDeserializer extends JsonDeserializer<OpenAiClient.ChatResponse.Choice.Message.Content> {
    @Override
    public OpenAiClient.ChatResponse.Choice.Message.Content deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        // 🎯 Случай 1: поле пришло как строка, содержащая JSON (некорректная сериализация)
        if (node.isTextual()) {
            String raw = node.asText();

            try {
                JsonNode parsed = mapper.readTree(raw); // пытаемся распарсить строку как JSON-объект
                return parseContent(parsed);
            } catch (Exception e) {
                // Если строка не JSON — fallback: берём как текст
                return new OpenAiClient.ChatResponse.Choice.Message.Content(DialogItem.Type.FINAL, raw, null);
            }
        }

        // 🎯 Случай 2: поле уже объект, как и должно быть
        if (node.isObject()) {
            return parseContent(node);
        }

        // 🔁 Fallback — пустой Content
        return new OpenAiClient.ChatResponse.Choice.Message.Content(DialogItem.Type.FINAL, "", null);
    }

    private OpenAiClient.ChatResponse.Choice.Message.Content parseContent(JsonNode node) {
        DialogItem.Type type = DialogItem.Type.FINAL;
        if (node.has("type")) {
            try {
                type = DialogItem.Type.valueOf(node.get("type").asText().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fallback to TEXT
            }
        }

        String content = node.has("content") ? node.get("content").asText() : null;
        String summary = node.has("summary") ? node.get("summary").asText(null) : null;

        return new OpenAiClient.ChatResponse.Choice.Message.Content(type, content, summary);
    }
}
