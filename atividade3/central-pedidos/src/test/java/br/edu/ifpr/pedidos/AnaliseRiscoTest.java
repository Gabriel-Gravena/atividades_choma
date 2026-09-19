package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnaliseRiscoTest {

    private final AnaliseRisco analise = new AnaliseRisco();

    @Test
    void deveRejeitarTotalNegativo() {
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(IllegalArgumentException.class,
            () -> analise.avaliar(cliente, -1, false));
    }

    @Test
    void deveRecusarClienteBloqueadoAntesDasOutrasRegras() {
        Cliente bloqueado = new Cliente(false, true, 0);

        assertEquals("RECUSADO", analise.avaliar(bloqueado, 200_000, true));
    }

    @Test
    void deveRevisarClienteNovoAcimaDeMilReais() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("REVISAO", analise.avaliar(novo, 100_001, false));
    }

    @Test
    void deveRevisarClienteNovoComEntregaExpressa() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("REVISAO", analise.avaliar(novo, 100_000, true));
    }

    @Test
    void deveAprovarClienteNovoNoLimiteComEntregaNormal() {
        Cliente novo = new Cliente(false, false, 0);

        assertEquals("APROVADO", analise.avaliar(novo, 100_000, false));
    }

    @Test
    void deveRevisarClienteRecorrenteComumAcimaDeCincoMilReais() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals("REVISAO", analise.avaliar(comum, 500_001, false));
    }

    @Test
    void deveAprovarVipRecorrenteMesmoAcimaDeCincoMilReais() {
        Cliente vip = new Cliente(true, false, 1);

        assertEquals("APROVADO", analise.avaliar(vip, 500_001, false));
    }

    @Test
    void deveAprovarClienteRecorrenteNoLimite() {
        Cliente comum = new Cliente(false, false, 1);

        assertEquals("APROVADO", analise.avaliar(comum, 500_000, false));
    }
}
