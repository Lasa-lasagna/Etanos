package com.lasa.gloria.common.user;

import com.lasa.gloria.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario crear(String username, String password, String nombre) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("El usuario ya existe", "USER_EXISTS", HttpStatus.CONFLICT);
        }
        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nombre(nombre)
                .estado(true)
                .build();
        return usuarioRepository.save(usuario);
    }
}
