package Muscle.auth.service;

import Muscle.auth.dto.RequestAuth;
import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.LoginFailedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;
import java.util.UUID;


@Service
@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:/secret/secret.properties")
public class NaverLoginService {

    private final AuthRepository authRepository;

    private final AuthService authService;

    private static final String NAVER_AUTH_BASE_URL = "https://nid.naver.com/oauth2.0/authorize";

    @Value("${oauth2.naver.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.naver.client-secret}")
    private String CLIENT_SECRET;
    private static final String REDIRECT_URI = "http://localhost:8080/api/auth/login/oauth2/code/naver";
    private static final String STATE_STRING = generateRandomUUID();  // CSRF 방지용 상태값
    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";


    private final RestTemplate restTemplate = new RestTemplate();




    // 네이버 로그인 URL 생성
    public String generateNaverLoginUrl() {
        return UriComponentsBuilder.fromHttpUrl(NAVER_AUTH_BASE_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("state", STATE_STRING)
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
    }

    // 네이버 로그인 처리
    public ResponseMessage processNaverLogin(String code, String state) {
        // 1. Authorization Code로 Access Token 요청
        String naverAccessToken = getAccessTokenFromNaver(code, state);

        // 2. Access Token으로 네이버 사용자 정보 요청
        ResponseAuth.OauthResponseDto oauthData = getUserInfoFromNaver(naverAccessToken);

        Auth user = authRepository.findByNaverId(oauthData.getOauthId());
        if(user != null){
            ResponseAuth.LoginUserRsDto response = naverLogin(user).orElseThrow(() -> new LoginFailedException());
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("Naver login successfully.")
                    .data(response)
                    .build();
            return responseMessage;
        }

        user = authRepository.findByEmail(oauthData.getEmail());
        if(user != null){
            //네이버 계정 연동
            ResponseAuth.LoginUserRsDto response = naverLinking(user, oauthData.getOauthId()).orElseThrow(() -> new LoginFailedException());
            ResponseMessage responseMessage = ResponseMessage.builder()
                    .message("User linked successfully with Naver.")
                    .data(response)
                    .build();
            return responseMessage;
        }
        // 네이버 회원가입
        String tempMuscleId = oauthData.getName() + oauthData.getEmail();
        ResponseAuth.LoginUserRsDto response = naverRegister(oauthData.getOauthId(), oauthData.getEmail(), oauthData.getName(), tempMuscleId).orElseThrow(() -> new LoginFailedException());
        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("User registered successfully with Naver.")
                .data(response)
                .build();
        return responseMessage;



    }


    public Optional<ResponseAuth.LoginUserRsDto> naverLogin (Auth user) {

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken, user.getId(), user.getRole()));
    }

    public Optional<ResponseAuth.LoginUserRsDto> naverRegister(String naverId, String email, String name, String muscleId) {
        Auth user = RequestAuth.naverRegister.toEntity(naverId, email, name, muscleId);
        authRepository.save(user);

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken, user.getId(), user.getRole()));

    }

    public Optional<ResponseAuth.LoginUserRsDto> naverLinking(Auth user, String naverId) {
        user.setNaverId(naverId);
        authRepository.save(user);

        String accessToken = authService.createAccessToken(user.getMuscleId());
        return Optional.ofNullable(ResponseAuth.LoginUserRsDto.toDto(accessToken, user.getId(), user.getRole()));
    }



    // 네이버에 Access Token 요청
    private String getAccessTokenFromNaver(String code, String state) {
        String requestUrl = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        try {
            // String 형으로 응답을 받음
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, null, String.class);
            String responseBody = response.getBody();

            // 응답 출력 (디버깅용)
            System.out.println("Naver Token Response: " + responseBody);

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

    // Access Token으로 네이버 사용자 정보 요청
    private ResponseAuth.OauthResponseDto getUserInfoFromNaver(String accessToken) {
        String requestUrl = "https://openapi.naver.com/v1/nid/me";

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

            if (rootNode.has("response")) {
                JsonNode userInfo = rootNode.path("response");


                String naverId = userInfo.path("id").asText();
                String email = userInfo.path("email").asText();
                String name = userInfo.path("name").asText();

                return new ResponseAuth.OauthResponseDto(naverId, name, email);
            } else {
                throw new RuntimeException("Failed to retrieve user info from Naver");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info response from Naver: " + e.getMessage(), e);
        }
    }

    private static String generateRandomUUID() {
        return UUID.randomUUID().toString(); // UUID 형식으로 랜덤 문자열 생성
    }


}
