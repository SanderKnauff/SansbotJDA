package ooo.sansk.sansbot.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

public class ChangeableFixedClock extends Clock {
    private Instant instant;
    private final ZoneId zone;

    public ChangeableFixedClock(Instant fixedInstant, ZoneId zone) {
        this.instant = fixedInstant;
        this.zone = zone;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        if (zone.equals(this.zone)) {  // intentional NPE
            return this;
        }
        return new ChangeableFixedClock(instant, zone);
    }

    @Override
    public long millis() {
        return instant.toEpochMilli();
    }

    @Override
    public Instant instant() {
        return instant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ChangeableFixedClock that = (ChangeableFixedClock) o;
        return instant.equals(that.instant) &&
                zone.equals(that.zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instant, zone);
    }

    @Override
    public String toString() {
        return "FixedClock[" + instant + "," + zone + "]";
    }
}
