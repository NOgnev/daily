package com.klaxon.daily.service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.klaxon.daily.client.OpenAiClientConfig;
import com.klaxon.daily.config.jackson.ContentDeserializer;
import com.klaxon.daily.dto.DialogItem;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "openAiClient",
        url = "https://api.openai.com/v1",
        configuration = OpenAiClientConfig.class
)
public interface OpenAiClient {

    @PostMapping("/moderations")
    ModerationResponse moderate(@RequestBody ModerationRequest request);

    @PostMapping("/chat/completions")
    ChatResponse chat(@RequestBody ChatRequest request);



    @Builder
    record ModerationRequest (String model,String input) {}

    @Builder
    record ModerationResponse (String id, String model, List<Result> results) {
        @Builder
        public record Result (boolean flagged, Map<String, Boolean> categories, Map<String, Double> category_scores){}
    }


    @Builder
    record ChatRequest(String model, double temperature, @JsonProperty("max_tokens") int maxTokens, List<Message> messages) {
        @Builder
        public record Message(DialogItem.Role role, String content) {}
    }

    @Builder
    record ChatResponse(String id, String object, List<Choice> choices) {
        @Builder
        public record Choice(int index, Message message) {
            @Builder
            public record Message(DialogItem.Role role, Content content) {
                @Builder
                @JsonDeserialize(using = ContentDeserializer.class)
                public record Content(DialogItem.Type type, String content, @Nullable String summary) {}
            }
        }
    }
}
