package spring.study.Member.infraStructure.rest.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class GoogleOAuthResponseDTO implements SocialOauthResponseDTO{
    private String access_token;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String scope;
    private String token_type;

}
