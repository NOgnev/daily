package com.klaxon.diary.config.log.hidden;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

public class HiddenBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            AnnotatedMember member = writer.getMember();

            Hidden hidden = member.getAnnotation(Hidden.class);
            if (hidden != null) {
                writer.assignSerializer(new HiddenSerializer(hidden.mask()));
            }
        }
        return beanProperties;
    }
}
