package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    @Test
    void deveRejeitarSubtotalNegativo() {
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(cliente, -1, null));
    }

    @Test
    void deveAplicarDezPorCentoParaVip() {
        Cliente vip = new Cliente(true, false, 1);

        assertEquals(1_000L, politica.calcular(vip, 10_000, null));
    }

    @Test
    void deveAplicarCincoPorCentoParaClienteComumNoLimite() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(2_500L, politica.calcular(comum, 50_000, "   "));
    }

    @Test
    void deveManterDescontoZeroParaClienteComumAbaixoDoLimite() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(0L, politica.calcular(comum, 49_999, null));
    }

    @Test
    void deveAplicarBemVindoNormalizadoParaPrimeiraCompra() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals(2_000L, politica.calcular(novo, 10_000, "  bemvindo  "));
    }

    @Test
    void naoDeveAplicarBemVindoQuandoJaExistemCompras() {
        Cliente recorrente = new Cliente(false, false, 1);

        assertEquals(0L, politica.calcular(recorrente, 10_000, "BEMVINDO"));
    }

    @Test
    void naoDeveAplicarBemVindoAbaixoDoSubtotalMinimo() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals(0L, politica.calcular(novo, 9_999, "BEMVINDO"));
    }

    @Test
    void deveAplicarExtraDezNoLimite() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(2_000L, politica.calcular(comum, 20_000, "EXTRA10"));
    }

    @Test
    void naoDeveAplicarExtraDezAbaixoDoLimite() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals(0L, politica.calcular(comum, 19_999, "EXTRA10"));
    }

    @Test
    void deveLimitarDescontoCombinadoA20PorCento() {
        Cliente vipNovo = new Cliente(true, false, 0);

        assertEquals(2_000L, politica.calcular(vipNovo, 10_000, "BEMVINDO"));
    }

    @Test
    void deveTruncarDivisaoPercentual() {
        Cliente vip = new Cliente(true, false, 1);

        assertEquals(1_000L, politica.calcular(vip, 10_001, null));
    }

    @Test
    void deveRejeitarCupomDesconhecido() {
        Cliente comum = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(comum, 10_000, "INVALIDO"));
    }
}
