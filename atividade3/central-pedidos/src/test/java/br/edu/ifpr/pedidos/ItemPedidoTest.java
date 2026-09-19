package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemPedidoTest {

    @Test
    void deveCalcularTotalEInformarEstoqueDisponivel() {
        ItemPedido item = new ItemPedido("LIVRO", 2_500, 3, 3, 400, false);

        assertEquals(7_500L, item.totalCentavos());
        assertTrue(item.disponivel());
    }

    @Test
    void deveInformarFaltaDeEstoque() {
        ItemPedido item = new ItemPedido("LIVRO", 2_500, 4, 3, 400, false);

        assertFalse(item.disponivel());
    }

    @Test
    void deveAceitarValoresNosLimitesSuperiores() {
        ItemPedido item = new ItemPedido("LIMITE", 1_000_000, 100, 0, 100_000, true);

        assertEquals(100_000_000L, item.totalCentavos());
    }

    @Test
    void deveRejeitarSkuNulo() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido(null, 100, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarSkuEmBranco() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("   ", 100, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarPrecoIgualAZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 0, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarPrecoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 1_000_001, 1, 1, 100, false));
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, -1, 1, 100, false));
    }

    @Test
    void deveRejeitarQuantidadeAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 101, 101, 100, false));
    }

    @Test
    void deveRejeitarEstoqueNegativo() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, -1, 100, false));
    }

    @Test
    void deveRejeitarPesoIgualAZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, 1, 0, false));
    }

    @Test
    void deveRejeitarPesoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 100, 1, 1, 100_001, false));
    }
}
