package jbnu.se.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OasisApiRequest {

    @JsonProperty("rType")
    private final String rType;

    @JsonProperty("loginType")
    private final String loginType;

    @JsonProperty("userNo")
    private final String userNo;

    @JsonProperty("userPwd")
    private final String userPwd;

    @JsonProperty("loginGubun")
    private final String loginGubun;

    @Builder
    public OasisApiRequest(String userNo, String userPwd) {
        this.rType = "3tier";
        this.loginType = "3tier";
        this.userNo = userNo;
        this.userPwd = userPwd;
        this.loginGubun = "0";
    }
}
