package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteTest {

    @Test
    void deveCriarClienteComHistoricoValido() {
        Cliente cliente = new Cliente(true, false, 3);

        assertAll(
            () -> assertTrue(cliente.vip()),
            () -> assertFalse(cliente.bloqueado()),
            () -> assertEquals(3, cliente.comprasAnteriores())
        );
    }

    @Test
    void deveAceitarHistoricoIgualAZero() {
        Cliente cliente = new Cliente(false, false, 0);

        assertEquals(0, cliente.comprasAnteriores());
    }

    @Test
    void deveRejeitarHistoricoNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> new Cliente(false, false, -1));
    }
}
