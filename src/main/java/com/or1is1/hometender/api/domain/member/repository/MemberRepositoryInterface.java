package com.or1is1.hometender.api.domain.member.repository;

import com.or1is1.hometender.api.dto.IsExistMemberResponse;

public interface MemberRepositoryInterface {
	public IsExistMemberResponse isExists(String loginId, String nickname);
}
