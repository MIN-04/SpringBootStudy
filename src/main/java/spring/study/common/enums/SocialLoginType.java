package spring.study.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialLoginType {
    GOOGLE("google", "https://accounts.google.com/o/oauth2/v2/auth", "http://localhost:8080/login/google/callback", "https://oauth2.googleapis.com/token"),
    KAKAO("kakao", "", "", ""),
    NAVER("naver", "", "", "");

    private final String socialType;
    private final String baseUrl;
    private final String redirectionUrl;
    private final String requestUrl;

}
