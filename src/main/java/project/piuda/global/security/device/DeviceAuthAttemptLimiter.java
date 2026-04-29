package project.piuda.global.security.device;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceAuthAttemptLimiter {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(10);

    private final Clock clock = Clock.systemUTC();
    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String deviceId) {
        AttemptState state = attempts.get(deviceId);
        if (state == null) {
            return false;
        }

        if (state.blockedUntil <= clock.millis()) {
            attempts.remove(deviceId);
            return false;
        }

        return state.failedAttempts >= MAX_FAILED_ATTEMPTS;
    }

    public void recordFailure(String deviceId) {
        attempts.compute(deviceId, (key, state) -> {
            if (state == null || state.blockedUntil <= clock.millis()) {
                return new AttemptState(1, clock.millis() + BLOCK_DURATION.toMillis());
            }

            return new AttemptState(state.failedAttempts + 1, clock.millis() + BLOCK_DURATION.toMillis());
        });
    }

    public void clear(String deviceId) {
        attempts.remove(deviceId);
    }

    private record AttemptState(int failedAttempts, long blockedUntil) {
    }
}
