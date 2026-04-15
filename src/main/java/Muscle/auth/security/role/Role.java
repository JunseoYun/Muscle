package Muscle.auth.security.role;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN","관리자 권한"),
    USER("ROLE_USER","회원 권한"),
    UNKNOWN("UNKNOWN","알 수 없는 권한");
    private String code;
    private String description;

    Role(String code, String description){
        this.code = code;
        this.description = description;
    }

    public static Role findByCode(String code){
        return Arrays.stream(Role.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }
}
