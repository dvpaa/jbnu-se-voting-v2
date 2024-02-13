package jbnu.se.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OasisApiRequest {

    @JsonProperty("rType")
    private final static String rType = "3tier";

    @JsonProperty("loginType")
    private final static String loginType = "3tier";

    @JsonProperty("userNo")
    private String userNo;

    @JsonProperty("userPwd")
    private String userPwd;

    @JsonProperty("loginGubun")
    private final String loginGubun = "0";


    @Builder
    public OasisApiRequest(String userNo, String userPwd) {
        this.userNo = userNo;
        this.userPwd = userPwd;
    }
}
