package Muscle.common.config;

import Muscle.auth.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        List<String> excludePatterns = Arrays.asList(
                "/api/auth/register","/api/auth/login","/dev","/h2/**","/ws/**","/api/chat/**",
                "/**",
                "/api/auth/sendEmail", "/api/auth/verifyEmail", // 이메일 인증 예외 처리
                "/api/auth/**", // 임시 토큰 발급 예외 처리
                "/swagger-ui/**", // 스웨거 UI 예외 처리
                "/swagger-resources/**", // 스웨거 리소스 예외 처리
                "/v3/api-docs/**" // 스웨거 API 문서 예외 처리
        );//interceptor 제외할 부분
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePatterns);

    }
}
