package Muscle.gymSearch;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gym")
public class GymSearchController {

    private final GymSearchService gymSearchService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;


    @GetMapping("/searchRandom")
    public String searchRandom(HttpServletRequest request, @RequestParam String query,
                                                  @RequestParam(defaultValue = "5") int display,
                                                  @RequestParam(defaultValue = "1") int start,
                                                  @RequestParam(defaultValue = "random") String sort) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }

        return gymSearchService.searchLocalGyms(token, query, display, start, sort);
    }

    @GetMapping("/searchComment")
    public String searchGyms(HttpServletRequest request, @RequestParam String query,
                                                  @RequestParam(defaultValue = "5") int display,
                                                  @RequestParam(defaultValue = "1") int start,
                                                  @RequestParam(defaultValue = "comment") String sort) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }

        return gymSearchService.searchLocalGyms(token, query, display, start, sort);


    }


}
