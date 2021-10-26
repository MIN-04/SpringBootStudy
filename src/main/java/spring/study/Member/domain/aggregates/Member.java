package spring.study.Member.domain.aggregates;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 테이블 ID

    //21.10.01 피드백 (10.05 수정)
    //회원 ID는 email로 잘 하지 않는다. → ID를 따로 만들어주기 (ID를 generic 하게 해서 이것만 PK)
    private String email; //이메일 (회원 ID)

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>(); //회원 권한

    private String provider; //제공사 (Naver, Google 등)

    @Embedded
    MemberBasicInfo memberBasicInfo;

    @Embedded
    MemberAddressInfo memberAddressInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return memberBasicInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
