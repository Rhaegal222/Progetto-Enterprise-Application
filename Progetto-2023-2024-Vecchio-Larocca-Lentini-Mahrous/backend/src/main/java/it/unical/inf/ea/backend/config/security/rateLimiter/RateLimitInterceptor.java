package it.unical.inf.ea.backend.config.security.rateLimiter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import it.unical.inf.ea.backend.config.security.JwtContextUtils;
import it.unical.inf.ea.backend.data.entities.User;
import it.unical.inf.ea.backend.exception.ManyRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final UserTypePlanService userTypePlanService;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userTypeKey;
        User userLogged = jwtContextUtils.getUserLoggedFromContext();
        if(userLogged != null){
            if(userLogged.isAdministrator()) {
                userTypeKey = "ADMIN-" + jwtContextUtils.getUserLoggedFromContext().getUsername();
            } else {
                userTypeKey = "BASIC-" + jwtContextUtils.getUserLoggedFromContext().getUsername();
            }
        }else {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            userTypeKey = "BASIC-" + ipAddress;
        }

        int cost = 5;
        String URI = request.getRequestURI();
        switch (URI) {
            case "/api/v1/images/users/photo-profile/**", "/api/v1/images/product/**" -> cost = 1;
        }

        Bucket tokenBucket = userTypePlanService.resolveBucket(userTypeKey);

        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(cost);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        }else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            throw new ManyRequestException("You have exhausted your API Request Quota");
        }
    }
}
