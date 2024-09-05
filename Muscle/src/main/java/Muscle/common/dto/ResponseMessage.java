package Muscle.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResponseMessage {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String message;
    private Object data;
}
