package spring.study.common.paths;

public class MemberUrl {
    /**
     * 21.10.15 피드백 (10.18 적용)
     * url의 공통부분 static 변수로 선언하고 사용
     */
    public static final String MEMBER_ROOT_PATH = "/member"; //루트 url

    public static final String MEMBERS_PATH = "/members"; //회원 수정, 회원 목록 조회
    public static final String MEMBERS_NEW = "/members/new"; //회원 가입
    public static final String MEMBERS_DELETE = "/members/{id}"; //회원 삭제

}
