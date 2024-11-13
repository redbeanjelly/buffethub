package com.hub.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.hub.domain.UserRole;
import com.hub.dto.UserDTO;
import com.hub.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("-------------JWTCheckFilter--------------");
        String authHeaderStr = request.getHeader("Authorization");
        log.info( "@@@@@",authHeaderStr);
        try {
            if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
                throw new RuntimeException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            }

            // Bearer accestoken...
            String accessToken = authHeaderStr.substring(7);
            
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            
            // claims에서 사용자 정보 추출
            String ur_id = (String) claims.get("urId");
            String ur_pw = (String) claims.get("urPw");
            String ur_nm = (String) claims.get("urNm");
            String ur_phn = (String) claims.get("urPhn");
            String ur_eml = (String) claims.get("urEml");
            UserRole ur_auth_code = UserRole.valueOf((String) claims.get("urAuthCode")); // String을 UserRole로 변환

            UserDTO userDTO = new UserDTO(ur_id, ur_pw, ur_nm, ur_phn, ur_eml, null, null, ur_auth_code, null, null);
            
            log.info("----------------------------------------");
            log.info("UserDTO 생성: {}", userDTO);
            log.info("사용자 권한: {}", userDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO,
                    ur_pw, userDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("인증 정보가 SecurityContext에 설정되었습니다: {}", authenticationToken);

        } catch (Exception e) {
            log.error("JWT Check Error: {}", e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }

        log.info("---------------------------");
        log.info("---------------------------");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Preflight 요청은 체크하지 않음
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String path = request.getRequestURI();
        log.info("Check uri: {}", path);

        // api/user/login, api/user/register 요청은 체크하지 않음
        if (path.startsWith("/api/user/login") || path.startsWith("/api/user/register")|| path.startsWith("/api/user/checkId")) {
            return true;
        }

        return false;
    }
}
