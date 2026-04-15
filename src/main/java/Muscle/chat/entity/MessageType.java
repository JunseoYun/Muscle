package Muscle.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("CHAT") CHAT,
    @JsonProperty("JOIN") JOIN,
    @JsonProperty("LEAVE") LEAVE,
    @JsonProperty("SYSTEM") SYSTEM
}

