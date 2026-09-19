package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoTest {

    @Test
    void deveCalcularSubtotalEPesoIgnorandoItemInativo() {
        ItemPedido ativo = item("ATIVO", 1_500, 2, 2, 300, false);
        ItemPedido inativo = item("INATIVO", 9_000, 0, 0, 900, true);
        Pedido pedido = new Pedido(List.of(ativo, inativo), "PR", false, null);

        assertAll(
            () -> assertEquals(3_000L, pedido.subtotalCentavos()),
            () -> assertEquals(600, pedido.pesoGramas())
        );
    }

    @Test
    void deveRetornarValoresNeutrosParaListaVazia() {
        Pedido pedido = new Pedido(List.of(), "PR", false, null);

        assertAll(
            () -> assertEquals(0L, pedido.subtotalCentavos()),
            () -> assertEquals(0, pedido.pesoGramas()),
            () -> assertFalse(pedido.temFragil()),
            () -> assertTrue(pedido.estoqueSuficiente())
        );
    }

    @Test
    void deveEncontrarItemFragilAtivoDepoisDeOutrosItens() {
        ItemPedido fragilInativo = item("INATIVO", 100, 0, 0, 100, true);
        ItemPedido ativoNaoFragil = item("COMUM", 100, 1, 1, 100, false);
        ItemPedido ativoFragil = item("FRAGIL", 100, 1, 1, 100, true);
        Pedido pedido = new Pedido(
            List.of(fragilInativo, ativoNaoFragil, ativoFragil), "SP", false, null);

        assertTrue(pedido.temFragil());
    }

    @Test
    void deveRetornarFalsoQuandoNaoHaItemFragilAtivo() {
        ItemPedido fragilInativo = item("INATIVO", 100, 0, 0, 100, true);
        ItemPedido ativoNaoFragil = item("COMUM", 100, 1, 1, 100, false);
        Pedido pedido = new Pedido(List.of(fragilInativo, ativoNaoFragil), "SP", false, null);

        assertFalse(pedido.temFragil());
    }

    @Test
    void deveConfirmarEstoqueQuandoTodasAsLinhasEstaoDisponiveis() {
        Pedido pedido = new Pedido(List.of(
            item("A", 100, 1, 1, 100, false),
            item("B", 100, 2, 5, 100, false)
        ), "RJ", false, null);

        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void deveInterromperBuscaQuandoPrimeiraLinhaNaoTemEstoque() {
        Pedido pedido = new Pedido(List.of(
            item("SEM", 100, 2, 1, 100, false),
            item("COM", 100, 1, 1, 100, false)
        ), "RJ", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveEncontrarFaltaDeEstoqueNaUltimaLinha() {
        Pedido pedido = new Pedido(List.of(
            item("COM", 100, 1, 1, 100, false),
            item("SEM", 100, 2, 1, 100, false)
        ), "RJ", false, null);

        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveCopiarListaDefensivamente() {
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(item("A", 100, 1, 1, 100, false));
        Pedido pedido = new Pedido(itens, "PR", false, null);

        itens.clear();

        assertEquals(1, pedido.itens().size());
        assertThrows(UnsupportedOperationException.class,
            () -> pedido.itens().add(item("B", 100, 1, 1, 100, false)));
    }

    @Test
    void deveRejeitarListaNula() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void deveRejeitarMaisDeCemLinhas() {
        List<ItemPedido> itens = Collections.nCopies(101,
            item("A", 100, 1, 1, 100, false));

        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(itens, "PR", false, null));
    }

    @Test
    void deveRejeitarElementoNulo() {
        List<ItemPedido> itens = Arrays.asList(
            item("A", 100, 1, 1, 100, false), null);

        assertThrows(NullPointerException.class,
            () -> new Pedido(itens, "PR", false, null));
    }

    @Test
    void deveRejeitarUfNula() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(List.of(), null, false, null));
    }

    @Test
    void deveRejeitarUfForaDoFormato() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(List.of(), "pr", false, null));
    }

    private ItemPedido item(String sku, long preco, int quantidade,
                            int estoque, int peso, boolean fragil) {
        return new ItemPedido(sku, preco, quantidade, estoque, peso, fragil);
    }
}
