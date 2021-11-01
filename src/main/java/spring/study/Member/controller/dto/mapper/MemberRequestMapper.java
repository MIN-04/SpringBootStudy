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
                .password(dto.getPassword())
                .build();
    }

    /**
     * 회원 가입 command 변환
     */
    public MemberCommand toCommand(MemberRequestJoinDTO dto){

        MemberBasicInfo basicVo = new MemberBasicInfo(
                dto.getName(),
                dto.getMobileNum(),
                dto.getGender(),
                dto.getBirth()
        );

        MemberAddressInfo addressVo = new MemberAddressInfo(
                dto.getAddress()
        );

        return MemberCommand.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .provider(dto.getProvider())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }

    /**
     * 회원 수정 command 변환
     */
    public MemberCommand toCommand(MemberRequestModifyDTO dto) {

        MemberBasicInfo basicVo = new MemberBasicInfo(
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
                .email(dto.getEmail())
                .password(dto.getPassword())
                .roles(dto.getRoles())
                .provider(dto.getProvider())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }
}
