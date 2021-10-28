package spring.study.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialLoginType {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String socialType;

}
