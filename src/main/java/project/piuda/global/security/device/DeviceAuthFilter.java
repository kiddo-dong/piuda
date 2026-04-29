package project.piuda.global.security.device;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.piuda.device.application.service.DeviceService;
import project.piuda.global.security.principal.DevicePrincipal;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceAuthFilter extends OncePerRequestFilter {

    private final DeviceService deviceService;
    private final DeviceAuthAttemptLimiter attemptLimiter;
    private static final int TOO_MANY_REQUESTS = 429;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (!uri.startsWith("/api/device/") || uri.equals("/api/device/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String deviceId = request.getHeader("X-DEVICE-ID");
        String deviceKey = request.getHeader("X-DEVICE-KEY");

        if (deviceId == null || deviceKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (attemptLimiter.isBlocked(deviceId)) {
            response.setStatus(TOO_MANY_REQUESTS);
            return;
        }

        try {
            DevicePrincipal principal = deviceService.validate(deviceId, deviceKey);
            attemptLimiter.clear(deviceId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("DEVICE"))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            attemptLimiter.recordFailure(deviceId);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
