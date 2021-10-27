package spring.study.common.paths;

public class LoginUrl {
    //루트 url
    public static final String LOGIN_ROOT_PATH = "/login";

    //소셜 로그인 타입 url
    public static final String LOGIN_SOCIAL_TYPE = "/{socialLoginType}";

    //Social Login API Server 요청에 의한 callback url
    public static final String LOGIN_SOCIAL_CALLBACK = "/{socialLoginType}/callback";
}
