package spring.study.Member.controller.dto.mapper;


import spring.study.Member.controller.dto.MemberRequestJoinDTO;
import spring.study.Member.controller.dto.MemberRequestLoginDTO;
import spring.study.Member.controller.dto.MemberRequestModifyDTO;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

public class MemberRequestMapper {

    /**
     * 로그인 command 변환
     */
    public MemberCommand toCommand(MemberRequestLoginDTO dto) {
        return MemberCommand.builder()
                .email(dto.getEmail())
                .basicInfo(MemberBasicInfo.builder()
                        .password(dto.getPassword())
                        .build())
                .build();
    }

    /**
     * 회원 가입 command 변환
     */
    public MemberCommand toCommand(MemberRequestJoinDTO dto){

        MemberBasicInfo basicVo = new MemberBasicInfo(
                dto.getPassword(),
                dto.getName(),
                dto.getMobileNum(),
                dto.getGender(),
                dto.getBirth()
        );

        MemberAddressInfo addressVo = new MemberAddressInfo(
                dto.getAddress()
        );

        return MemberCommand.builder()
                .provider(dto.getProvider())
                .email(dto.getEmail())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }

    /**
     * 회원 수정 command 변환
     */
    public MemberCommand toCommand(MemberRequestModifyDTO dto) {

        MemberBasicInfo basicVo = new MemberBasicInfo(
                dto.getPassword(),
                dto.getName(),
                dto.getMobileNum(),
                dto.getGender(),
                dto.getBirth()
        );

        MemberAddressInfo addressVo = new MemberAddressInfo(
                dto.getAddress()
        );

        return MemberCommand.builder()
                .id(dto.getId())
                .roles(dto.getRoles())
                .provider(dto.getProvider())
                .email(dto.getEmail())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }
}
