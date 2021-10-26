package spring.study.Member.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MemberCommand {
    private final Long id; //id (회원 PKP
    private final List<String> roles; //회원 권한
    private final String provider; //제공사
    private final String email; //이메일 (회원 ID)
    private final MemberBasicInfo basicInfo;
    private final MemberAddressInfo addressInfo;
}
