package com.livevote.analyticsconsumer.analyticsconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PollOptionDto {
    private String optionId;

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
