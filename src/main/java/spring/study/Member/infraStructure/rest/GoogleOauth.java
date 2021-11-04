package spring.study.Member.infraStructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.Member.domain.services.SocialOauth;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.enums.SocialLoginType;

import static spring.study.common.enums.SocialLoginType.GOOGLE;

@Component
@Profile("sns_oauth")
public class GoogleOauth implements SocialOauth {

    @Value("${google.client_id}")
    private String GOOGLE_SNS_CLINET_ID;
    @Value("${google.client_secret}")
    private String GOOGLE_SNS_CLINET_SECRET;
    @Value("${google.base_url}")
    private String GOOGLE_BASE_URL;
    @Value("${google.redirect_url}")
    private String GOOGLE_REDIRECT_URL;
    @Value("${google.request_url}")
    private String GOOGLE_REQUEST_URL;
    @Value("${google.request_user_info}")
    private String GOOGLE_REQUEST_USERINFO;

    @Override
    public SocialLoginType getSocialOauthName() {
        return GOOGLE;
    }

    @Override
    public String getOauthRedirectUrl() {

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("scope", "email profile");
            add("response_type", "code");
            add("client_id", GOOGLE_SNS_CLINET_ID);
            add("redirect_uri", GOOGLE_REDIRECT_URL);
            add("access_type", "offline");
        }};

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_BASE_URL)
                .queryParams(query);

        return builder.toUriString();
    }

    @Override
    public ResponseEntity<GoogleOAuthResponseDTO> requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", GOOGLE_SNS_CLINET_ID);
            add("client_secret", GOOGLE_SNS_CLINET_SECRET);
            add("redirect_uri", GOOGLE_REDIRECT_URL);
            add("grant_type", "authorization_code");
        }};

        // TODO : RestTemplate -> WebFlux 사용
        //ResponseEntity<GoogleOAuthResponseDTO> responseEntity = restTemplate.postForEntity(GOOGLE_REQUEST_URL, query, GoogleOAuthResponseDTO.class);

        return restTemplate.postForEntity(GOOGLE_REQUEST_URL, query, GoogleOAuthResponseDTO.class);

    }

    /**
     * Google에 User Info 요청
     */
    @Override
    public ResponseEntity<GoogleUserInfo> requestUserInfo(GoogleOAuthResponseDTO dto) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer" + dto.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        return restTemplate.exchange(GOOGLE_REQUEST_USERINFO, HttpMethod.GET, request, GoogleUserInfo.class);
    }
}
