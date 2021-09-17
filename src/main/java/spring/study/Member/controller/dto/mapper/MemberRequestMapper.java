package spring.study.Member.controller.dto.mapper;


import spring.study.Member.controller.dto.MemberJoinRequestDTO;
import spring.study.Member.controller.dto.MemberModifyRequestDTO;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

public class MemberRequestMapper {

    /**
     * 회원 가입 command 변환
     */
    public MemberCommand toCommand(MemberJoinRequestDTO dto){

        MemberBasicInfo basicVo = new MemberBasicInfo(
                dto.getPassword(),
                dto.getName(),
                dto.getMobileNum(),
                dto.getGender(),
                dto.getBirthday()
        );

        MemberAddressInfo addressVo = new MemberAddressInfo(
                dto.getAddress()
        );

        return MemberCommand.builder()
                .email(dto.getEmail())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }

    /**
     * 회원 수정 command 변환
     */
    public MemberCommand toCommand(MemberModifyRequestDTO dto) {

        MemberBasicInfo basicVo = new MemberBasicInfo(
                dto.getPassword(),
                dto.getName(),
                dto.getMobileNum(),
                dto.getGender(),
                dto.getBirthday()
        );

        MemberAddressInfo addressVo = new MemberAddressInfo(
                dto.getAddress()
        );

        return MemberCommand.builder()
                .email(dto.getEmail())
                .basicInfo(basicVo)
                .addressInfo(addressVo)
                .build();
    }
}
