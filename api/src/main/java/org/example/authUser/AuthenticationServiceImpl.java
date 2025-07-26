//package org.example.authUser;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.BadRequestException;
//import org.example.user.UserService;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.token.TokenService;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuthenticationServiceImpl implements AuthenticationService {
//
//    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
//    private final UserService usuarioService;
//    private final TokenService tokenService;
//    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    public void login(HttpServletRequest request, HttpServletResponse response) {
//        var login = request.getParameter("login");
//        var password = request.getParameter("password");
//
//        log.info(EnumMessage.AUTHENTICATION_LOGIN_ATTEMPT.getMessage(login));
//        var authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
//        Authentication auth;
//        try {
//            auth = authenticationManager.authenticate(authenticationToken);
//        } catch (AuthenticationException e) {
//            throw new ForbiddenException(EnumMessage.AUTHENTICATION_LOGIN_FAILURE.getMessage());
//        }
//
//        log.info(EnumMessage.AUTHENTICATION_LOGIN_SUCCESS.getMessage());
//        var authenticatedUser = (User) auth.getPrincipal();
//        var userId = userDetailsService.findIdByLogin(authenticatedUser.getUsername());
//        var accessToken = tokenService.generateAccessToken(userId);
//        var refreshToken = tokenService.generateRefreshToken(userId);
//        response.setHeader("access_token", accessToken);
//        response.setHeader("refresh_token", refreshToken);
//    }
//
//    @Override
//    public Usuario getAuthenticatedUser() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        return usuarioService.findByIdOrThrow(Integer.valueOf((String) authentication.getPrincipal()));
//    }
//
//    @Override
//    public Integer getAuthenticatedUserId() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        return Integer.valueOf((String) authentication.getPrincipal());
//    }
//
//    @Override
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//        var authorizationHeader = request.getHeader(REFRESH_TOKEN_HEADER);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            try {
//                var oldRefreshToken = authorizationHeader.substring("Bearer ".length());
//                if (!tokenService.isTokenValid(oldRefreshToken)) {
//                    throw new AccessDeniedException(
//                            EnumMessage.AUTHENTICATION_INVALID_REFRESH_TOKEN.getMessage());
//                }
//                var userId = tokenService.extractId(oldRefreshToken);
//                var user = usuarioService.findById(Integer.valueOf(userId));
//                var newAccessToken = tokenService.generateAccessToken(user.id());
//                var newRefreshToken = tokenService.generateRefreshToken(user.id());
//                response.setHeader("access_token", newAccessToken);
//                response.setHeader("refresh_token", newRefreshToken);
//            } catch (Exception e) {
//                var errorResponse = new ApiErrorResponse(SC_UNAUTHORIZED, e.getMessage());
//                response.setContentType(APPLICATION_JSON_VALUE);
//                response.setStatus(SC_UNAUTHORIZED);
//                response.getWriter().write(tokenService.toJson(errorResponse));
//            }
//        } else {
//            throw new RuntimeException(EnumMessage.AUTHENTICATION_REFRESH_TOKEN_NOT_FOUND.getMessage());
//        }
//    }
//}
