package spring.study.Member.controller.dto.mapper;


import spring.study.Member.controller.dto.MemberRequestDTO;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

public class MemberRequestMapper {

    /**
     * dto command 변환
     */
    public MemberCommand toCommand(MemberRequestDTO dto){

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

//    /**
//     * 회원 수정 command 변환
//     */
//    public MemberCommand toCommand(MemberModifyRequestDTO dto) {
//
//        MemberBasicInfo basicVo = new MemberBasicInfo(
//                dto.getPassword(),
//                dto.getName(),
//                dto.getMobileNum(),
//                dto.getGender(),
//                dto.getBirthday()
//        );
//
//        MemberAddressInfo addressVo = new MemberAddressInfo(
//                dto.getAddress()
//        );
//
//        return MemberCommand.builder()
//                .email(dto.getEmail())
//                .basicInfo(basicVo)
//                .addressInfo(addressVo)
//                .build();
//    }
}
