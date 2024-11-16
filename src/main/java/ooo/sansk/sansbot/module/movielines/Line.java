package ooo.sansk.sansbot.module.movielines;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Line(
        @JsonProperty("sub") String text,
        @JsonProperty("time") String time
) {
}
