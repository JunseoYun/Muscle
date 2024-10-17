package Muscle.gymSearch;

import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymSearchService {
    private static final String CLIENT_ID = "7IkNK1KlVLSR0SAiMAiS";
    private static final String CLIENT_SECRET = "A1fpi0bgFt";
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public String searchLocalGyms(Optional<String> token, String query, int display, int start, String sort) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin == null) {
            throw new IllegalArgumentException("비회원 접근 제어");
        }

        String apiUrl = "https://openapi.naver.com/v1/search/local.json?query=" + query + "헬스장"
                + "&display=" + display
                + "&start=" + start
                + "&sort=" + sort;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", CLIENT_ID);
        headers.add("X-Naver-Client-Secret", CLIENT_SECRET);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        System.out.println(response.getBody());
        return response.getBody();  // JSON 형태로 결과 반환
    }
}
