package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PagamentoServiceTest {

    @Test
    void deveRejeitarProcessadorNulo() {
        assertThrows(NullPointerException.class,
            () -> new PagamentoService(null));
    }

    @Test
    void deveRejeitarTotalNaoPositivo() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(IllegalArgumentException.class,
            () -> service.pagar(0, 1));
    }

    @Test
    void deveRejeitarLimiteMenorQueUm() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(IllegalArgumentException.class,
            () -> service.pagar(100, 0));
    }

    @Test
    void deveRejeitarLimiteMaiorQueTres() {
        PagamentoService service = new PagamentoService(total -> true);

        assertThrows(IllegalArgumentException.class,
            () -> service.pagar(100, 4));
    }

    @Test
    void deveAprovarNaPrimeiraTentativa() {
        List<Long> cobrancas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            cobrancas.add(total);
            return true;
        });

        assertTrue(service.pagar(12_345, 3));
        assertEquals(List.of(12_345L), cobrancas);
    }

    @Test
    void deveRecusarImediatamenteSemRepetir() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.incrementAndGet();
            return false;
        });

        assertFalse(service.pagar(12_345, 3));
        assertEquals(1, chamadas.get());
    }

    @Test
    void deveAprovarDepoisDeIndisponibilidadeTemporaria() {
        List<Long> cobrancas = new ArrayList<>();
        PagamentoService service = new PagamentoService(total -> {
            cobrancas.add(total);
            if (cobrancas.size() == 1) {
                throw new IllegalStateException("temporariamente indisponível");
            }
            return true;
        });

        assertTrue(service.pagar(9_900, 3));
        assertEquals(List.of(9_900L, 9_900L), cobrancas);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void deveRetornarFalsoAoEsgotarTentativas(int limite) {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.incrementAndGet();
            throw new IllegalStateException("temporariamente indisponível");
        });

        assertFalse(service.pagar(5_000, limite));
        assertEquals(limite, chamadas.get());
    }

    @Test
    void devePropagarExcecaoQueNaoRepresentaIndisponibilidade() {
        PagamentoService service = new PagamentoService(total -> {
            throw new UnsupportedOperationException("falha definitiva");
        });

        assertThrows(UnsupportedOperationException.class,
            () -> service.pagar(5_000, 3));
    }
}
