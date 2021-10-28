package spring.study.common.auth;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.common.enums.SocialLoginType;

import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_ID;
import static spring.study.common.enums.SocialLoginType.GOOGLE;
import static spring.study.common.paths.SocialOauthUrl.GOOGLE_BASE_URL;
import static spring.study.common.paths.SocialOauthUrl.GOOGLE_REDIRECT_URL;

public class GoogleOauth implements SocialOauth{
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
    public SocialLoginType getSocialOauthName() {
        return GOOGLE;
    }
}
