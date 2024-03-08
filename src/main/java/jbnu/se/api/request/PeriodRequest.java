package jbnu.se.api.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidDateRange;
import jbnu.se.api.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(message = "선거 시작 날짜는 종료 날짜보다 앞으로 설정해 주세요.", groups = ValidationGroups.FourthGroup.class)
public class PeriodRequest {
    @NotNull(message = "선거 시작 날짜를 입력해 주세요.", groups = ValidationGroups.SecondGroup.class)
    @FutureOrPresent(message = "과거 날짜로는 설정 할 수 없습니다.", groups = ValidationGroups.ThirdGroup.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "선거 종료 날짜를 입력해 주세요.", groups = ValidationGroups.SecondGroup.class)
    @FutureOrPresent(message = "과거 날짜로는 설정 할 수 없습니다.", groups = ValidationGroups.ThirdGroup.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
}
