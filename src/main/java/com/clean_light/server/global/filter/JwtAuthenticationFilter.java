package com.clean_light.server.global.filter;

import com.clean_light.server.jwt.dto.UserTokenInfo;
import com.clean_light.server.jwt.repository.BlackListTokenRepository;
import com.clean_light.server.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final BlackListTokenRepository blackListRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerAccessToken = request.getHeader("Authorization");
        String bearerRefreshToken = request.getHeader("Refresh-Token");

        if (bearerAccessToken == null && bearerRefreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // accessToken 검증
        if (bearerAccessToken != null) {
            if (!bearerAccessToken.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰은 \"Bearer \"로 시작해야합니다");
                return;
            }

            try {
                String accessToken = bearerAccessToken.substring(7);
                boolean isBlackList = blackListRedisRepository.isBlackList(accessToken);

                if (isBlackList) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용할 수 없는 토큰입니다.");
                    return;
                }

                jwtService.decodeToken(accessToken);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }
        }

        // refreshToken 검증
        if (bearerRefreshToken != null) {
            if (!bearerRefreshToken.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰은 \"Bearer \"로 시작해야합니다");
                return;
            }

            try {
                String refreshToken = bearerAccessToken.substring(7);
                boolean isBlackList = blackListRedisRepository.isBlackList(refreshToken);

                if (isBlackList) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용할 수 없는 토큰입니다.");
                    return;
                }

                jwtService.decodeToken(refreshToken);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}