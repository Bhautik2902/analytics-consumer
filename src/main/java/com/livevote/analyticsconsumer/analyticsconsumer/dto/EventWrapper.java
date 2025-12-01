package com.livevote.analyticsconsumer.analyticsconsumer.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class EventWrapper {

    @JsonProperty("payload")
    private LinkedHashMap<String, Object> payload;

    public LinkedHashMap<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(LinkedHashMap<String, Object> payload) {
        this.payload = payload;
    }
}
