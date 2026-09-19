package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveCalcularMediaInteira() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(6, 8);

        assertEquals(7.0, resultado, 0.0001);
    }

    @Test
    void deveCalcularMediaComParteDecimal() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(7, 8);

        assertEquals(7.5, resultado, 0.0001);
    }

    @Test
    void deveAprovarAlunoNoLimiteSete() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(7);

        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveColocarEmRecuperacaoAlunoAbaixoDeSete() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(6.99);

        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveColocarEmRecuperacaoAlunoNoLimiteQuatro() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(4);

        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoAbaixoDeQuatro() {
        Boletim boletim = new Boletim();

        String resultado = boletim.verificarSituacao(3.99);

        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveRetornarZeroQuandoNaoHaMedias() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {});

        assertEquals(0, resultado);
    }

    @Test
    void deveContarUmaAprovacaoComUmaIteracao() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {7});

        assertEquals(1, resultado);
    }

    @Test
    void deveContarAprovadosComVariasIteracoes() {
        Boletim boletim = new Boletim();

        int resultado = boletim.contarAprovados(new double[] {8, 5, 7, 3.5});

        assertEquals(2, resultado);

    }
}
