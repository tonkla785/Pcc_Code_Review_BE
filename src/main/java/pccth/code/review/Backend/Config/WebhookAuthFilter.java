package pccth.code.review.Backend.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class WebhookAuthFilter extends OncePerRequestFilter {

    private final WebhookConfig webhookConfig;

    public WebhookAuthFilter(WebhookConfig webhookConfig) {
        this.webhookConfig = webhookConfig;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/webhooks/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = request.getHeader("WEBHOOK-TOKEN");

        if (token == null || !token.equals(webhookConfig.getWebhookToken())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid webhook token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

