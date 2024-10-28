package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.LoginFailedException;
import Muscle.common.exception.error.RegisterFailedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.Optional;
import java.util.UUID;

@Service
@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:/secret/secret.properties")
public class KakaoLoginService {
    private final AuthRepository authRepository;

    private final AuthService authService;

    private static final String KAKAO_AUTH_BASE_URL = "https://kauth.kakao.com/oauth/authorize";


    @Value("${oauth2.kakao.client-id}")
    private String CLIENT_ID;
    private static final String REDIRECT_URI = "http://localhost:8080/api/auth/login/oauth2/code/kakao";
    private static final String STATE_STRING = generateRandomUUID();  // CSRF 방지용 상태값
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate = new RestTemplate();


    // 카카오 로그인 URL 생성
    public String generateKakaoLoginUrl () {
        return UriComponentsBuilder.fromHttpUrl(KAKAO_AUTH_BASE_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("state", STATE_STRING)
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
    }

    //카카오 로그인 처리
    public ResponseMessage processKakaoLogin(String code, String state) {

        // 1. Authorization Code로 Access Token 요청
        String kakaoAccessToken = getAccessTokenFromKakao(code, state);
        // 2. Access Token으로 카카오 사용자 정보 요청
        ResponseAuth.OauthResponseDto oauthData = getUserInfoFromKakao(kakaoAccessToken);


        Auth user = authRepository.findByKakaoId(oauthData.getOauthId());
        if(user != null) {
            ResponseAuth.LoginUserRsDto response = kakaoLogin(user).orElseThrow(() -> new LoginFailedException());
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("Kakao login successfully.")
                    .data(response)
                    .build();
            return responseMessage;
        }

        user = authRepository.findByEmail(oauthData.getEmail());
        if(user != null) {
            //카카오 계정 연동
            ResponseAuth.LoginUserRsDto response = kakaoLinking(user, oauthData.getOauthId()).orElseThrow(() -> new LoginFailedException());
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("User linked successfully with Kakao.")
                    .data(response)
                    .build();
            return responseMessage;
        }

        //카카오 회원가입
        String tempMuscleId = oauthData.getName() + oauthData.getEmail();
        ResponseAuth.LoginUserRsDto response = kakaoRegister(oauthData.getOauthId(), oauthData.getEmail(), oauthData.getName(), tempMuscleId).orElseThrow(() -> new LoginFailedException());
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User registered successfully with Kakao.")
                .data(response)
                .build();
        return responseMessage;

    }


    // 카카오에 Access Token 요청
    private String getAccessTokenFromKakao(String code, String state) {
        String requestUrl = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        try {
            // String 형으로 응답을 받음
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, null, String.class);
            String responseBody = response.getBody();

            // 응답 출력 (디버깅용)
            System.out.println("Kakao Token Response: " + responseBody);

            // ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 필요한 값 추출
            String accessToken = rootNode.path("access_token").asText();
            String refreshToken = rootNode.path("refresh_token").asText();
            String tokenType = rootNode.path("token_type").asText();
            String expiresIn = rootNode.path("expires_in").asText();



            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve access token from Naver: " + e.getMessage(), e);
        }
    }

    private ResponseAuth.OauthResponseDto getUserInfoFromKakao(String accessToken) {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(requestUrl,
                HttpMethod.POST,
                naverProfileRequest,
                String.class);


        String responseBody = response.getBody();

        System.out.println("responseBody = " + responseBody);
        // ObjectMapper를 사용하여 JSON 파싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            String kakaoId = rootNode.path("id").asText();
            String name = rootNode.path("properties").path("nickname").asText();
            String email = rootNode.path("kakao_account").path("email").asText();


            if(email == null) {
                throw new RuntimeException("Set up your Kakao email.");
            }
            if (name == null) {
                System.out.println("Name NULL");
                name = email.split("@")[0];
            }


            return new ResponseAuth.OauthResponseDto(kakaoId, name, email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info response from Kakao: " + e.getMessage(), e);
        }
    }

    public Optional<ResponseAuth.LoginUserRsDto> kakaoLogin (Auth user) {

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken));
    }

    public Optional<ResponseAuth.LoginUserRsDto> kakaoRegister(String kakaoId, String email, String name, String muscleId) {
        Auth user = RequestAuth.kakaoRegister.toEntity(kakaoId, email, name, muscleId);
        authRepository.save(user);

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken));

    }

    public Optional<ResponseAuth.LoginUserRsDto> kakaoLinking(Auth user, String kakaoId) {
        user.setKakaoId(kakaoId);
        authRepository.save(user);

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken));
    }

    private static String generateRandomUUID() {
        return UUID.randomUUID().toString(); // UUID 형식으로 랜덤 문자열 생성
    }
}
