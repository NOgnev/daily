package com.klaxon.daily.config.log.hidden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Sanitizer {

    private static final ObjectMapper SANITIZED_MAPPER = createSanitizer();

    private static ObjectMapper createSanitizer() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        BeanSerializerModifier modifier = new HiddenBeanSerializerModifier();
        module.setSerializerModifier(modifier);
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }

    public static String toJson(Object object) {
        try {
            return SANITIZED_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            return "\"[error serializing]\"";
        }
    }
}
