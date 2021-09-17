package spring.study.Member.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

@Getter
@AllArgsConstructor
@Builder
public class MemberCommand {
    private final String email; //이메일 (회원 ID)
    private final MemberBasicInfo basicInfo;
    private final MemberAddressInfo addressInfo;
}
