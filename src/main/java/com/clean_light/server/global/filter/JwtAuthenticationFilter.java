package com.clean_light.server.global.filter;

import com.clean_light.server.jwt.repository.BlackListTokenRepository;
import com.clean_light.server.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final BlackListTokenRepository blackListRedisRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> routes = List.of("/jwt/refresh");

        return routes.contains("/jwt/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerAccessToken = request.getHeader("Authorization");
        String bearerRefreshToken = request.getHeader("Refresh-Token");

        if (bearerAccessToken == null && bearerRefreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // accessToken 검증
        if (bearerAccessToken != null && !isValidToken(bearerAccessToken, response)) {
            return;
        }

        // refreshToken 검증
        if (bearerRefreshToken != null && !isValidToken(bearerRefreshToken, response)) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String bearerToken, HttpServletResponse response) throws IOException {
        if (!bearerToken.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰은 \"Bearer \"로 시작해야합니다");
            return false;
        }

        String token = bearerToken.substring(7);
        try {
            boolean isBlackList = blackListRedisRepository.isBlackList(token);

            if (isBlackList) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용할 수 없는 토큰입니다.");
                return false;
            }

            jwtService.decodeToken(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return false;
        }

        return true;
    }
}