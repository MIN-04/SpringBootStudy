package spring.study.Member.infraStructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.Member.domain.services.SocialOauth;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.common.enums.SocialLoginType;

import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_ID;
import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_SECRET;
import static spring.study.common.enums.SocialLoginType.GOOGLE;
import static spring.study.common.paths.SocialOauthUrl.*;

@Component
public class GoogleOauth implements SocialOauth {

    @Override
    public SocialLoginType getSocialOauthName() {
        return GOOGLE;
    }

    @Override
    public String getOauthRedirectUrl() {

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("scope", "profile");
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
    public GoogleOAuthResponseDTO requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", GOOGLE_SNS_CLINET_ID);
            add("client_secret", GOOGLE_SNS_CLINET_SECRET);
            add("redirect_uri", GOOGLE_REDIRECT_URL);
            add("grant_type", "authorization_code");
        }};

        ResponseEntity<GoogleOAuthResponseDTO> responseEntity = restTemplate.postForEntity(GOOGLE_REQUEST_URL, query, GoogleOAuthResponseDTO.class);
        System.out.println("responseEntity.getBody() = " + responseEntity.getBody());

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }else {
            // TODO : 예외 처리
            return null;
        }
    }
}
