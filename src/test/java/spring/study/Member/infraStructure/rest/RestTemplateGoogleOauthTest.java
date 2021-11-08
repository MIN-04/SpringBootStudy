package spring.study.Member.infraStructure.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.Member.domain.services.SocialOauth;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @RestClientTest
 * 요청 하는 쪽 입장에서의 테스트
 * 원하는 형태로 Json 결과를 반환해줄 수 있는 Mock 서버
 */
@RestClientTest(GoogleOauth.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[rest > GoogleOuth] Google 로그인 API Test")
@Profile("sns_oauth")
@Disabled
class RestTemplateGoogleOauthTest {

    /*@InjectMocks
    GoogleOauth googleOauth;

    @Mock
    RestTemplate restTemplate;*/

    @Autowired
    GoogleOauth googleOauth;

    RestTemplate restTemplate;

    MockRestServiceServer mockServer;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    /*@Autowired
    MockRestServiceServer mockRestServiceServer;*/

    @Value("${google.client_id}")
    private String GOOGLE_SNS_CLINET_ID;
    @Value("${google.client_secret}")
    private String GOOGLE_SNS_CLINET_SECRET;
    @Value("${google.base_url}")
    private String GOOGLE_BASE_URL;
    @Value("${google.redirect_url}")
    private String GOOGLE_REDIRECT_URL;
    @Value("${google.request_url}")
    private String GOOGLE_REQUEST_URL;
    @Value("${google.request_user_info}")
    private String GOOGLE_REQUEST_USERINFO;

    @Test
    @DisplayName("AccessToken 요청 성공")
    void successAccessToken() throws JsonProcessingException {
        System.out.println("GOOGLE_REQUEST_URL = "+GOOGLE_REQUEST_URL);
        //given
        /*String code = "code";

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", GOOGLE_SNS_CLINET_ID);
            add("client_secret", GOOGLE_SNS_CLINET_SECRET);
            add("redirect_uri", GOOGLE_REDIRECT_URL);
            add("grant_type", "authorization_code");
        }};

        GoogleOAuthResponseDTO responseDTO = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();

        ResponseEntity<GoogleOAuthResponseDTO> responseEntity =
                ResponseEntity.ok(responseDTO);

        mockServer.expect(requestTo("http://localhost:8080/" + query))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(responseDTO)));

        *//*given(restTemplate.postForEntity(GOOGLE_REQUEST_URL, query, GoogleOAuthResponseDTO.class))
                .willReturn(responseEntity);*//*
//        willReturn(responseEntity).given(restTemplate).postForEntity(any(), anyString(), GoogleOAuthResponseDTO.class);

        //when
        ResponseEntity<GoogleOAuthResponseDTO> result =
                googleOauth.requestAccessToken(code);

        //then*/
    }

    /*@Test
    @DisplayName("AccessToken 요청 성공")
    void successAccessToken() {
        //given
        String code = "code";

        MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
            add("code", code);
            add("client_id", GOOGLE_SNS_CLINET_ID);
            add("client_secret", GOOGLE_SNS_CLINET_SECRET);
            add("redirect_uri", GOOGLE_REDIRECT_URL);
            add("grant_type", "authorization_code");
        }};

        GoogleOAuthResponseDTO responseDTO = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();

        ResponseEntity<GoogleOAuthResponseDTO> responseEntity =
                ResponseEntity.ok(responseDTO);

        mockRestServiceServer.expect(requestTo(GOOGLE_REQUEST_URL + query))
                .andRespond(withSuccess(responseEntity, ));
        //when
        //then
    }*/

}