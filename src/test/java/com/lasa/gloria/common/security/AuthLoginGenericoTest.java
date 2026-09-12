package com.lasa.gloria.common.security;

import com.lasa.gloria.common.user.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthLoginGenericoTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UsuarioService usuarioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void login_fallido_no_distingue_usuario_inexistente_de_clave_erronea() throws Exception {
        usuarioService.crear("juan", "clave123", "Juan");

        MvcResult usuarioInexistente = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nadie\",\"password\":\"cualquiera\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MvcResult claveErronea = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"juan\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String bodyInexistente = sinTimestamp(usuarioInexistente.getResponse().getContentAsString());
        String bodyClaveMala = sinTimestamp(claveErronea.getResponse().getContentAsString());

        // Misma respuesta genérica en ambos casos: no filtra si el usuario existe
        assertThat(bodyInexistente).isEqualTo(bodyClaveMala);
        assertThat(bodyInexistente).contains("Credenciales inválidas", "BAD_CREDENTIALS");
        assertThat(bodyInexistente).doesNotContain("nadie", "juan", "mala", "cualquiera", "no encontrado");
    }

    @Test
    void login_exitoso_devuelve_204_con_cookie_y_sin_token_en_body() throws Exception {
        usuarioService.crear("maria", "clave123", "Maria");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maria\",\"password\":\"clave123\"}"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("JWT=");
        assertThat(result.getResponse().getContentAsString()).isEmpty();
    }

    private static String sinTimestamp(String body) {
        return body.replaceAll(",\"timestamp\":\"[^\"]*\"", "");
    }
}
