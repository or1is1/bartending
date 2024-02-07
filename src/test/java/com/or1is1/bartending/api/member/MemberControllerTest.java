package com.or1is1.bartending.api.member;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MemberController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MemberControllerTest {
    private final ObjectMapper objectMapper;
    private final String email;
    private final String password;
    private final String nickname;
    private final String url;

    @MockBean
    private MemberService memberService;
    @Autowired
    private MockMvc mockMvc;


    // 공통 사용 요소 초기화
    public MemberControllerTest() {
        objectMapper = new ObjectMapper();
        email = "gildong@gmail.com";
        password = "hong1443";
        nickname = "홍길동";
        url = "/api/members";
    }

    @Test
    @DisplayName("정상적인 회원가입 처리")
    void join() throws Exception {
        // given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = objectMapper.writeValueAsString(memberJoinRequest);

        // when
        when(memberService.join(any(MemberJoinRequest.class)))
                .thenReturn(new MemberJoinResponse(1L, nickname));

        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname));
    }

    @Test
    @DisplayName("올바른 형식의 이메일로 회원가입을 해야 한다.")
    void joinWithWrongEmail() throws Exception {
        // given
        String email = "gildong#gmail.com";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = objectMapper.writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("Email");
        assertThat(objectError.getDefaultMessage()).isEqualTo("잘못된 이메일 형식입니다.");
    }

    @Test
    @DisplayName("비밀번호는 비어 있으면 안된다.")
    void joinWithEmptyPassword() throws Exception {
        // given
        String password = "      ";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("NotBlank");
        assertThat(objectError.getDefaultMessage()).isEqualTo("공백은 허용되지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호는 4글자보다 길어야 한다.")
    void joinWithTooShortPassword() throws Exception {
        // given
        String password = "ho3";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("Size");
        assertThat(objectError.getDefaultMessage()).isEqualTo("비밀번호는 4자 이상, 20자 미만이여야 합니다.");
    }

    @Test
    @DisplayName("비밀번호는 20자보다 짧아야 한다.")
    void joinWithTooLongPassword() throws Exception {
        // given
        String password = "hong1443hong1443hong14";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("Size");
        assertThat(objectError.getDefaultMessage()).isEqualTo("비밀번호는 4자 이상, 20자 미만이여야 합니다.");
    }

    @Test
    @DisplayName("닉네임은 비어있으면 안된다.")
    void joinWithEmptyNickname() throws Exception {
        // given
        String nickname = "      ";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("NotBlank");
        assertThat(objectError.getDefaultMessage()).isEqualTo("공백은 허용되지 않습니다.");
    }

    @Test
    @DisplayName("닉네임의 길이는 2자보다 길어야 한다.")
    void joinWithTooShortNickname() throws Exception {
        // given
        String nickname = "홍";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("Size");
        assertThat(objectError.getDefaultMessage()).isEqualTo("닉네임은 2자 이상, 10자 미만이여야 합니다.");
    }

    @Test
    @DisplayName("닉네임의 길이는 10자보다 짧아야 한다")
    void joinWithTooLongNickname() throws Exception {
        // given
        String nickname = "홍길동홍길동홍길동홍길";
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(email, password, nickname);
        String content = new ObjectMapper().writeValueAsString(memberJoinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(content));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isBadRequest())
                .andReturn();
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isExactlyInstanceOf(MethodArgumentNotValidException.class);

        BindingResult bindingResult = ((MethodArgumentNotValidException) resolvedException).getBindingResult();
        assertThat(bindingResult.getErrorCount()).isEqualTo(1);

        ObjectError objectError = bindingResult.getAllErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("Size");
        assertThat(objectError.getDefaultMessage()).isEqualTo("닉네임은 2자 이상, 10자 미만이여야 합니다.");
    }
}
