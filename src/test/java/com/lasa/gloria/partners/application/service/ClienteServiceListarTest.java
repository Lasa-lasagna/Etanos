package com.lasa.gloria.partners.application.service;

import com.lasa.gloria.partners.domain.model.Cliente;
import com.lasa.gloria.partners.domain.model.TipoDocumento;
import com.lasa.gloria.partners.domain.repository.ClienteRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClienteServiceListarTest {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ClienteService clienteService;

    @Test
    void listar_devuelve_solo_clientes_activos() {
        // Arrange: dato independiente de la implementación - un activo y un inactivo
        Cliente activo = clienteRepository.save(Cliente.builder()
                .tipoDoc(TipoDocumento.DNI)
                .nroDoc("11111111")
                .nombre("Cliente Activo")
                .estado(true)
                .limiteCredito(BigDecimal.ZERO)
                .diasCredito(0)
                .build());

        Cliente inactivo = clienteRepository.save(Cliente.builder()
                .tipoDoc(TipoDocumento.DNI)
                .nroDoc("22222222")
                .nombre("Cliente Inactivo")
                .estado(false)
                .limiteCredito(BigDecimal.ZERO)
                .diasCredito(0)
                .build());

        // Act: llamar a la interfaz pública bajo prueba (seam: ClienteService.listar)
        Page<?> pagina = clienteService.listar(PageRequest.of(0, 10));

        // Assert: especificación - debe contener solo el activo
        assertThat(pagina.getContent())
                .extracting("nroDoc")
                .contains("11111111")
                .doesNotContain("22222222");

        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void listar_no_devuelve_clientes_eliminados_por_soft_delete() {
        Cliente aEliminar = clienteRepository.save(Cliente.builder()
                .tipoDoc(TipoDocumento.RUC)
                .nroDoc("33333333")
                .nombre("Por Eliminar")
                .estado(true)
                .limiteCredito(BigDecimal.ZERO)
                .diasCredito(0)
                .build());

        // soft delete via servicio (pone estado=false)
        clienteService.eliminar(aEliminar.getId());

        Page<?> pagina = clienteService.listar(PageRequest.of(0, 10));

        assertThat(pagina.getContent())
                .extracting("nroDoc")
                .doesNotContain("33333333");
    }
}
