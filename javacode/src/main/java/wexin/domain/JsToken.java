package wexin.domain;

import lombok.Data;

@Data
public class JsToken {
    private String errorcode;
    private String access_token;
    private Long expires_in;
}
