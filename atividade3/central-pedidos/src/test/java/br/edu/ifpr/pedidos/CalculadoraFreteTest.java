package br.edu.ifpr.pedidos;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraFreteTest {

    private final CalculadoraFrete calculadora = new CalculadoraFrete();
    private final Cliente comum = new Cliente(false, false, 1);

    @Test
    void deveRejeitarValorLiquidoNegativo() {
        Pedido pedido = pedido("PR", false, item("A", 1_000, 1, 1_000, false));

        assertThrows(IllegalArgumentException.class,
            () -> calculadora.calcular(pedido, comum, -1));
    }

    @ParameterizedTest
    @CsvSource({
        "PR, 1200",
        "SP, 2000",
        "RJ, 2000",
        "SC, 3000"
    })
    void deveAplicarTarifaBaseDaUf(String uf, long esperado) {
        Pedido pedido = pedido(uf, false, item("A", 1_000, 1, 1_000, false));

        assertEquals(esperado, calculadora.calcular(pedido, comum, 10_000));
    }

    @ParameterizedTest
    @CsvSource({
        "2000, 1200",
        "2001, 1500",
        "3000, 1500",
        "3001, 1800"
    })
    void deveCobrarPesoExcedentePorQuiloOuFracao(int peso, long esperado) {
        Pedido pedido = pedido("PR", false, item("PESO", 1_000, 1, peso, false));

        assertEquals(esperado, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void deveZerarBaseEPesoParaEntregaNormalNoLimiteDeGratuidade() {
        Pedido pedido = pedido("SC", false, item("PESO", 40_000, 1, 4_001, false));

        assertEquals(0L, calculadora.calcular(pedido, comum, 30_000));
    }

    @Test
    void naoDeveDarGratuidadeParaEntregaExpressa() {
        Pedido pedido = pedido("PR", true, item("A", 40_000, 1, 1_000, false));

        assertEquals(2_700L, calculadora.calcular(pedido, comum, 30_000));
    }

    @Test
    void deveCobrarMetadeDaBaseEDoPesoParaVip() {
        Cliente vip = new Cliente(true, false, 1);
        Pedido pedido = pedido("PR", false, item("PESO", 1_000, 1, 3_000, false));

        assertEquals(750L, calculadora.calcular(pedido, vip, 10_000));
    }

    @Test
    void deveAcrescentarFragilidadeUmaUnicaVez() {
        ItemPedido primeiro = item("F1", 1_000, 1, 500, true);
        ItemPedido segundo = item("F2", 1_000, 1, 500, true);
        Pedido pedido = new Pedido(List.of(primeiro, segundo), "PR", false, null);

        assertEquals(1_700L, calculadora.calcular(pedido, comum, 10_000));
    }

    @Test
    void naoDeveCobrarFragilidadeDeItemInativo() {
        Pedido pedido = pedido("PR", false, item("F", 1_000, 0, 500, true));

        assertEquals(1_200L, calculadora.calcular(pedido, comum, 10_000));
    }

    private Pedido pedido(String uf, boolean expresso, ItemPedido item) {
        return new Pedido(List.of(item), uf, expresso, null);
    }

    private ItemPedido item(String sku, long preco, int quantidade,
                            int peso, boolean fragil) {
        return new ItemPedido(sku, preco, quantidade, quantidade, peso, fragil);
    }
}
