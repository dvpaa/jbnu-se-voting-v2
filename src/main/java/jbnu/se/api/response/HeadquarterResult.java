package jbnu.se.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class HeadquarterResult {
    private final String symbol;

    private final String name;

    private final Long count;

    @Builder
    public HeadquarterResult(String symbol, String name, Long count) {
        this.symbol = symbol;
        this.name = name;
        this.count = count;
    }
}
