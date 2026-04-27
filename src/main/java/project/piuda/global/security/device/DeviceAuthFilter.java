package project.piuda.global.security.device;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.piuda.device.application.service.DeviceService;
import project.piuda.device.domain.Device;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceAuthFilter extends OncePerRequestFilter {

    private final DeviceService deviceService;

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

        try {
            Device device = deviceService.validate(deviceId, deviceKey);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(device, null, List.of());

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}