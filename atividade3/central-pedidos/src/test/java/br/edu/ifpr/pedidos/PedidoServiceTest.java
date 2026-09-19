package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            // A lista comprova uma única cobrança, com o valor correto.
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveRejeitarProcessadorNulo() {
        assertThrows(NullPointerException.class,
            () -> new PedidoService(null));
    }

    @Test
    void deveRejeitarPedidoNulo() {
        PedidoService service = new PedidoService(total -> true);
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(NullPointerException.class,
            () -> service.fechar(null, cliente));
    }

    @Test
    void deveRejeitarClienteNulo() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = pedido(item("A", 1_000, 1, 1, 100, false), false, null);

        assertThrows(NullPointerException.class,
            () -> service.fechar(pedido, null));
    }

    @Test
    void deveBloquearAntesDeAvaliarEstoqueCupomOuPagamento() {
        Cliente bloqueado = new Cliente(false, true, 0);
        Pedido pedido = pedido(item("SEM", 1_000, 2, 0, 100, false), false, "INVALIDO");
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, bloqueado);

        assertSemCobranca("BLOQUEADO", resultado);
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRejeitarPedidoSemItensAtivosSemCobrar() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = pedido(item("INATIVO", 1_000, 0, 0, 100, false), false, null);
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });

        assertThrows(IllegalArgumentException.class,
            () -> service.fechar(pedido, cliente));
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRetornarSemEstoqueAntesDeAvaliarCupomOuPagamento() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = pedido(item("SEM", 1_000, 2, 1, 100, false), false, "INVALIDO");
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertSemCobranca("SEM_ESTOQUE", resultado);
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRetornarRevisaoSemCobrar() {
        Cliente novo = new Cliente(false, false, 0);
        Pedido pedido = pedido(item("A", 10_000, 1, 1, 1_000, false), true, null);
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, novo);

        assertAll(
            () -> assertEquals("REVISAO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(2_700L, resultado.freteCentavos()),
            () -> assertEquals(12_700L, resultado.totalCentavos()),
            () -> assertEquals(0, chamadas.get())
        );
    }

    @Test
    void deveRetornarPagamentoRecusadoComValoresCalculados() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = pedido(item("A", 10_000, 1, 1, 1_000, false), false, null);
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return false;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveEsgotarTresTentativasDePagamento() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = pedido(item("A", 10_000, 1, 1, 1_000, false), false, null);
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            throw new IllegalStateException("temporariamente indisponível");
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertEquals("PAGAMENTO_RECUSADO", resultado.status());
        assertEquals(3, chamadas.get());
    }

    @Test
    void deveIntegrarDescontoExtraEFreteVip() {
        Cliente vip = new Cliente(true, false, 1);
        Pedido pedido = pedido(item("A", 20_000, 1, 1, 1_000, false), false, "EXTRA10");
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, vip);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(20_000L, resultado.subtotalCentavos()),
            () -> assertEquals(4_000L, resultado.descontoCentavos()),
            () -> assertEquals(600L, resultado.freteCentavos()),
            () -> assertEquals(16_600L, resultado.totalCentavos()),
            () -> assertEquals(List.of(16_600L), cobrancas)
        );
    }

    @Test
    void devePropagarCupomInvalidoSemCobrar() {
        Cliente cliente = new Cliente(false, false, 1);
        Pedido pedido = pedido(item("A", 10_000, 1, 1, 100, false), false, "INVALIDO");
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });

        assertThrows(IllegalArgumentException.class,
            () -> service.fechar(pedido, cliente));
        assertEquals(0, chamadas.get());
    }

    private Pedido pedido(ItemPedido item, boolean expresso, String cupom) {
        return new Pedido(List.of(item), "PR", expresso, cupom);
    }

    private ItemPedido item(String sku, long preco, int quantidade,
                            int estoque, int peso, boolean fragil) {
        return new ItemPedido(sku, preco, quantidade, estoque, peso, fragil);
    }

    private void assertSemCobranca(String status, ResultadoPedido resultado) {
        assertAll(
            () -> assertEquals(status, resultado.status()),
            () -> assertEquals(0L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(0L, resultado.totalCentavos())
        );
    }
}
