package com.lasa.gloria.common.security;

import com.lasa.gloria.common.user.Usuario;
import com.lasa.gloria.common.user.UsuarioRepository;
import com.lasa.gloria.common.user.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    private ResponseCookie buildCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(jwtService.getCookieName(), token)
                .httpOnly(true)
                .secure(jwtService.isCookieSecure())
                .sameSite(jwtService.getCookieSameSite())
                .path(jwtService.getCookiePath())
                .maxAge(maxAgeSeconds)
                .build();
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        long maxAge = jwtService.getExpirationMs() / 1000;
        ResponseCookie cookie = buildCookie(token, maxAge);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        Usuario usuario = usuarioService.crear(request.username(), request.password(), request.nombre());
        String token = jwtService.generateToken(usuario.getUsername(), usuario.getId());
        addJwtCookie(response, token);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String token = jwtService.generateToken(usuario.getUsername(), usuario.getId());
        addJwtCookie(response, token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String token = null;
        if (httpRequest.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : httpRequest.getCookies()) {
                if (jwtService.getCookieName().equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        if (token == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String username = jwtService.extractUsername(token);
        if (!jwtService.isTokenValid(token, username)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String newToken = jwtService.generateToken(usuario.getUsername(), usuario.getId());
        addJwtCookie(httpResponse, newToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Opción A: solo borrar cookie (token sigue válido hasta expiración 30min)
        ResponseCookie cookie = buildCookie("", 0);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElse(null);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("username", authentication.getName());
        res.put("userId", usuario != null ? usuario.getId() : null);
        res.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        return res;
    }
}
