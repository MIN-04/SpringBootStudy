package spring.study.Member.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.study.Member.domain.services.SocialOauth;
import spring.study.common.enums.SocialLoginType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SocialOauthFactory {

    private Map<SocialLoginType, SocialOauth> socialOauthMap;

    @Autowired
    public SocialOauthFactory(Set<SocialOauth> socialOauthSet) {
        createSocialOauth(socialOauthSet);
    }

    public SocialOauth findSocialOauthType(SocialLoginType type) {
        return socialOauthMap.get(type);
    }

    private void createSocialOauth(Set<SocialOauth> socialOauthSet) {
        socialOauthMap = new HashMap<SocialLoginType, SocialOauth>();
        socialOauthSet.forEach(
                s -> socialOauthMap.put(s.getSocialOauthName(), s)
        );
    }
}
