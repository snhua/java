package wexin.domain;

import lombok.Data;

@Data
public class AuthToken {

    private String access_token;
    private Long expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    private Long errcode;
    private String errmsg;

}
