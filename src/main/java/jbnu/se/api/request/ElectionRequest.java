package jbnu.se.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidDateRange;
import jbnu.se.api.annotation.ValidElectionType;
import jbnu.se.api.validation.ValidationGroups.FirstGroup;
import jbnu.se.api.validation.ValidationGroups.FourthGroup;
import jbnu.se.api.validation.ValidationGroups.SecondGroup;
import jbnu.se.api.validation.ValidationGroups.ThirdGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ElectionRequest {

    @NotBlank(message = "제목을 입력해 주세요.", groups = FirstGroup.class)
    private String title;

    @NotNull(message = "선거 기간을 입력해 주세요.", groups = FirstGroup.class)
    @Valid
    private Period period;

    @NotNull(message = "선거 종류를 입력해 주세요.", groups = FirstGroup.class)
    @ValidElectionType(message = "유효하지 않은 선거 종류 입니다.", groups = SecondGroup.class)
    private String electionType;

    @Getter
    @Setter
    @ValidDateRange(message = "선거 시작 날짜는 종료 날짜보다 앞으로 설정해 주세요.", groups = FourthGroup.class)
    public static class Period {
        @NotNull(message = "선거 시작 날짜를 입력해 주세요.", groups = SecondGroup.class)
        @FutureOrPresent(message = "과거 날짜로는 설정 할 수 없습니다.", groups = ThirdGroup.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startDate;

        @NotNull(message = "선거 종료 날짜를 입력해 주세요.", groups = SecondGroup.class)
        @FutureOrPresent(message = "과거 날짜로는 설정 할 수 없습니다.", groups = ThirdGroup.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endDate;
    }
}
