package jbnu.se.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OasisApiResponse {
    private List<User> users;

    @Getter
    @Setter
    public static class User {
        @JsonProperty("USERNO")
        private String studentId;

        @JsonProperty("USERNM")
        private String studentName;
    }
}