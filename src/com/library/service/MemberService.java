package com.library.service;

import com.library.dao.MemberDAO;
import com.library.exception.DuplicateMemberNoException;
import com.library.model.Member;

import java.util.List;
import java.util.Optional;

/**
 * 會員管理業務邏輯（F2）。輸入驗證 ＋ 重複檢查後委派 DAO。
 */

public class MemberService {

    private final MemberDAO memberDao;

    public MemberService(MemberDAO memberDao) {
        this.memberDao = memberDao;
    }

    /**
     * 新增會員。
     * @throws IllegalArgumentException     欄位不合法
     * @throws DuplicateMemberNoException   會員編號已存在
     */
    public Member addMember(Member member) {
        requireText(member.getMemberNo(), "會員編號不可為空");
        requireText(member.getName(), "姓名不可為空");
        if (member.getType() == null) {
            throw new IllegalArgumentException("請指定會員身份");
        }

        if (memberDao.findByMemberNo(member.getMemberNo()).isPresent()) {
            throw new DuplicateMemberNoException(member.getMemberNo());
        }

        memberDao.insert(member);
        return member;
    }

    /** 依會員編號查詢。 */
    public Optional<Member> findByMemberNo(String memberNo) {
        return memberDao.findByMemberNo(memberNo);
    }

    /** 全部會員。 */
    public List<Member> listAll() {
        return memberDao.findAll();
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

}
