package com.library.model;

/**
 * 會員實體，對應資料表 {@code members} 的一列。
 * 借閱上限與延長借期委派給 {@link MemberType}。
 */

public class Member {
    
    private long id;
    private String memberNo;
    private String name;
    private MemberType type;
    private String email;

    public Member() {
    }

    /** 新增會員用：尚未有 id。 */
    public Member(String memberNo, String name, MemberType type, String email) {
        this.memberNo = memberNo;
        this.name = name;
        this.type = type;
        this.email = email;
    }

    /** 從資料庫載入用：帶完整欄位。 */
    public Member(long id, String memberNo, String name, MemberType type, String email) {
        this.id = id;
        this.memberNo = memberNo;
        this.name = name;
        this.type = type;
        this.email = email;
    }

    // ── 委派 MemberType 的業務規則 ───────────────────────────

    /** 同時借書上限（本）。 */
    public int borrowLimit() {
        return type.borrowLimit();
    }

    /** 借期延長天數。 */
    public int extraDays() {
        return type.extraDays();
    }

    // ── getter / setter ─────────────────────────────────────

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s（%s，%s）上限 %d 本",
                id, name, memberNo, type.label(), borrowLimit());
    }

}
