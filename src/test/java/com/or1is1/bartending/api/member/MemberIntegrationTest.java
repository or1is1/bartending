package com.or1is1.bartending.api.member;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.or1is1.bartending.api.member.dto.MemberJoinRequest;
import com.or1is1.bartending.api.member.dto.MemberLoginRequest;
import com.or1is1.bartending.api.member.dto.MemberWithdrawRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.or1is1.bartending.api.StringConst.LOGIN_MEMBER;
import static java.util.Locale.KOREAN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberIntegrationTest {
	private final String loginId;
	private final String password;
	private final String nickname;
	private final String url;

	@Autowired
	MockMvc mockMvc;
	@Autowired
	MessageSource messageSource;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	MemberService memberService;

	public MemberIntegrationTest() {
		loginId = "loginId";
		password = "password!";
		nickname = "nickname";
		url = "/api/members";
	}

	@Test
	@DisplayName("회원가입 성공")
	void join() throws Exception {
		// given
		MemberJoinRequest memberJoinRequest = new MemberJoinRequest(loginId, password, nickname);
		String content = objectMapper.writeValueAsString(memberJoinRequest);

		// when
		ResultActions resultActions = mockMvc.perform(post(url)
				.contentType(APPLICATION_JSON)
				.content(content));

		// then
		resultActions.andExpectAll(
				status().isOk(),
				jsonPath("$.data.nickname").value(nickname)
		);
	}

	@Test
	@DisplayName("로그인 성공")
	void login() throws Exception {
		// given
		MemberJoinRequest memberJoinRequest = new MemberJoinRequest(loginId, password, nickname);
		String joinContent = objectMapper.writeValueAsString(memberJoinRequest);

		mockMvc.perform(post(url)
				.contentType(APPLICATION_JSON)
				.content(joinContent));

		MemberLoginRequest memberLoginRequest = new MemberLoginRequest(loginId, password);
		String loginContent = objectMapper.writeValueAsString(memberLoginRequest);

		// when
		ResultActions resultActions = mockMvc.perform(post(url + "/login")
				.contentType(APPLICATION_JSON)
				.content(loginContent));

		// then
		resultActions.andExpectAll(
				status().isOk(),
				jsonPath("$.data.nickname").value(nickname)
		);
	}

	@Test
	@DisplayName("로그인 실패 - 회원 정보 불일치")
	void loginFail() throws Exception {
		// given
		MemberLoginRequest memberLoginRequest = new MemberLoginRequest(loginId, password);
		String content = objectMapper.writeValueAsString(memberLoginRequest);

		// when
		ResultActions resultActions = mockMvc.perform(post(url + "/login")
				.contentType(APPLICATION_JSON)
				.content(content));

		// then
		resultActions.andExpectAll(
				status().isBadRequest(),
				jsonPath("$.message").value(messageSource.getMessage("member.login.fail", null, KOREAN))
		);
	}

	@Test
	@DisplayName("로그아웃")
	void logout() throws Exception {
		// given
		Member member = new Member(loginId, password, nickname);
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(LOGIN_MEMBER, member);

		// when
		ResultActions resultActions = mockMvc.perform(post(url + "/logout")
				.contentType(APPLICATION_JSON)
				.session(mockHttpSession));

		// then
		resultActions.andExpectAll(
				status().isOk(),
				jsonPath("$.data.isInvalidated").value(true)
		);
	}

	@Test
	@DisplayName("로그아웃 - 기존 로그인 회원 없음")
	void logoutFail() throws Exception {
		// when
		ResultActions resultActions = mockMvc.perform(post(url + "/logout")
				.contentType(APPLICATION_JSON));

		// then
		resultActions.andExpectAll(
				status().isOk(),
				jsonPath("$.data.isInvalidated").value(false)
		);
	}

	@Test
	@DisplayName("회원 탈퇴")
	void withdraw() throws Exception {
		// given
		join();

		MemberWithdrawRequest memberWithdrawRequest = new MemberWithdrawRequest(password);
		String content = objectMapper.writeValueAsString(memberWithdrawRequest);

		// when
		ResultActions resultActions = mockMvc.perform(delete(url + "/" + loginId)
				.contentType(APPLICATION_JSON)
				.content(content));

		// then
		resultActions.andExpectAll(
				status().isOk(),
				jsonPath("$.data").value(true)
		);
	}

	@Test
	@DisplayName("회원 탈퇴 실패")
	void withdrawFail() throws Exception {
		// given
		MemberWithdrawRequest memberWithdrawRequest = new MemberWithdrawRequest(password);
		String content = objectMapper.writeValueAsString(memberWithdrawRequest);

		// when
		ResultActions resultActions = mockMvc.perform(delete(url + "/" + loginId)
				.contentType(APPLICATION_JSON)
				.content(content));

		// then
		resultActions.andExpectAll(
				status().isBadRequest(),
				jsonPath("$.message").value(messageSource.getMessage("member.withdraw.fail", null, KOREAN))
		);
	}
}
