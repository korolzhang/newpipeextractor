package org.schabi.newpipe.extractor.account;

/**
 * created by lijinping on 2021/10/11 14:56
 * desc:
 */
public class AccountInfo {

    private String name;
    private String email;
    private String avatar;

    public AccountInfo() {

    }

    public AccountInfo(String name, String email, String avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }
}
