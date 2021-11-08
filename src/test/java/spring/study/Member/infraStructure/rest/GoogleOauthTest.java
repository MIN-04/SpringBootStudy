package spring.study.Member.infraStructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("[rest] GoogleOauth 테스트")
@Profile("sns_oauth")
//@RestClientTest(GoogleOauth.class)
class GoogleOauthTest {

    @InjectMocks
    GoogleOauth googleOauth;

    static MockWebServer mockWebServer;

    ObjectMapper mapper = new ObjectMapper();

    String baseUrl;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    }

    @Test
    @DisplayName("리다이렉트Url 요청 성공")
    void successGetOauthRedirectUrl() {
        //given
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("scope", "email profile");
            add("response_type", "code");
            add("client_id", "GOOGLE_SNS_CLINET_ID");
            add("redirect_uri", "GOOGLE_REDIRECT_URL");
            add("access_type", "offline");
        }};

        UriComponentsBuilder testBuilder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/login/google/callback")
                .queryParams(query);

        ReflectionTestUtils.setField(googleOauth,
                "GOOGLE_BASE_URL" ,"http://localhost:8080/login/google/callback");
        ReflectionTestUtils.setField(googleOauth, "GOOGLE_SNS_CLINET_ID", "GOOGLE_SNS_CLINET_ID");
        ReflectionTestUtils.setField(googleOauth, "GOOGLE_REDIRECT_URL", "GOOGLE_REDIRECT_URL");


        //when
        String resultUrl = googleOauth.getOauthRedirectUrl();

        //then
        assertThat(resultUrl).isEqualTo(testBuilder.toUriString());
    }
    
    @Test
    @DisplayName("Google AccessToken 받아오기 성공")
    void successRequestAccessToken() throws Exception {
        //given
        String code = "testCode";

        GoogleOAuthResponseDTO dto = GoogleOAuthResponseDTO.builder()
                .accessToken("testToken")
                .build();

        //requestAccessToken에 사용되는 queryParam
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", "GOOGLE_SNS_CLINET_ID");
            add("client_secret", "GOOGLE_SNS_CLINET_SECRET");
            add("redirect_uri", "GOOGLE_REDIRECT_URL");
            add("grant_type", "authorization_code");
        }};

        ReflectionTestUtils.setField(googleOauth, "GOOGLE_REQUEST_URL", baseUrl);

        ReflectionTestUtils.setField(googleOauth, "GOOGLE_SNS_CLINET_ID", "GOOGLE_SNS_CLINET_ID");
        ReflectionTestUtils.setField(googleOauth, "GOOGLE_SNS_CLINET_SECRET", "GOOGLE_SNS_CLINET_SECRET");
        ReflectionTestUtils.setField(googleOauth, "GOOGLE_REDIRECT_URL", "GOOGLE_REDIRECT_URL");

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(dto)));

        //when
        ResponseEntity<GoogleOAuthResponseDTO> resultResponse = googleOauth.requestAccessToken(code);
        RecordedRequest request = mockWebServer.takeRequest();

        //then
        //queryParam 비교
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(request.getRequestUrl().toString());
        MultiValueMap<String, String> resultQuery = url.build().getQueryParams();
        assertThat(resultQuery).isEqualTo(query);

        //Http Method 비교
        assertThat(request.getMethod()).isEqualTo("POST");

        //상태값 비교
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //accessToken 비교
        assertThat(Objects.requireNonNull(resultResponse.getBody()).getAccessToken()).isEqualTo(dto.getAccessToken());
    }

    @Test
    @DisplayName("회원 정보 요청 성공")
    void successRequestUserInfo() throws Exception {
        //given
        ReflectionTestUtils.setField(googleOauth,"GOOGLE_REQUEST_USERINFO", baseUrl);

        GoogleOAuthResponseDTO dto = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();

        GoogleUserInfo userInfo = GoogleUserInfo.builder()
                .email("hong@google.com")
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Authorization", "Bearer" + dto.getAccessToken())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(userInfo)));

        //when
        ResponseEntity<GoogleUserInfo> resultResponse = googleOauth.requestUserInfo(dto);
        RecordedRequest request = mockWebServer.takeRequest();

        //then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer"+dto.getAccessToken());
        assertThat(resultResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(resultResponse.getBody()).getEmail()).isEqualTo(userInfo.getEmail());
    }

}