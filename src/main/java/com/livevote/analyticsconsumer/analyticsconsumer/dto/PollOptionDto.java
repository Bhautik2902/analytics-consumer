package com.livevote.analyticsconsumer.analyticsconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PollOptionDto {
    @JsonProperty("optionId")
    private String optionId;

    @JsonProperty("text")
    private String text;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
