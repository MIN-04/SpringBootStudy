package spring.study.Member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_ID;
import static spring.study.common.enums.SocialLoginType.GOOGLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    /**
     * RedirectURL 만드는 메서드
     * @param socialLoginType
     */
    public String request(String  socialLoginType) throws IOException {

        switch (socialLoginType) {
            case "google": {
                MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
                    add("scope", "profile");
                    add("response_type", "code");
                    add("client_id", GOOGLE_SNS_CLINET_ID);
                    add("redirect_uri", GOOGLE.getRedirectionUrl());
                }};

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE.getBaseUrl())
                        .queryParams(query);

                /**
                 * 해당 SNS로 로그인 할 수 있는 페이지로 이동되는 URL
                 */
                return builder.toUriString();
            }
            case "kakao":
                return null;

            default:
                throw new IllegalStateException("알 수 없는 소셜 로그인 형식입니다.");
        }
    }

}
