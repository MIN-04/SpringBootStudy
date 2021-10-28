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
import spring.study.common.exceptions.CustomException;

import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_ID;
import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_SECRET;
import static spring.study.common.enums.ErrorCode.FAIL_LOGIN;
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

    /**
     * 21.10.27 피드백 (10.28 수정 완료)
     * requestAcessTocken의 return null 부분은 실패인데 Controller는 항상 성공코드를 넣는다.
     * -> 실패 시 예외처리
     */
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

        // TODO : RestTemplate -> WebFlux 사용
        ResponseEntity<GoogleOAuthResponseDTO> responseEntity = restTemplate.postForEntity(GOOGLE_REQUEST_URL, query, GoogleOAuthResponseDTO.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }else {
            throw new CustomException(FAIL_LOGIN);
        }
    }
}
