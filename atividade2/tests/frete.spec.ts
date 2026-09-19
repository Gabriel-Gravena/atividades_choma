import { test, expect } from '@playwright/test';

const casosFrete = [
  {
    nome: 'calcula R$ 15,00 para CEP iniciado por 8 e pedido abaixo de R$ 200',
    cep: '81234567',
    valor: '199,99',
    mensagem: 'Frete: R$ 15,00',
    role: 'status',
  },
  {
    nome: 'calcula R$ 25,00 para os demais CEPs e pedido abaixo de R$ 200',
    cep: '12345678',
    valor: '199,99',
    mensagem: 'Frete: R$ 25,00',
    role: 'status',
  },
  {
    nome: 'oferece frete grátis no limite de R$ 200,00',
    cep: '81234567',
    valor: '200,00',
    mensagem: 'Frete grátis',
    role: 'status',
  },
  {
    nome: 'oferece frete grátis acima de R$ 200,00',
    cep: '12345678',
    valor: '250,00',
    mensagem: 'Frete grátis',
    role: 'status',
  },
  {
    nome: 'rejeita CEP com menos de 8 dígitos',
    cep: '8123456',
    valor: '100,00',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
  {
    nome: 'rejeita CEP com caracteres não numéricos',
    cep: '81234ABC',
    valor: '100,00',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
  {
    nome: 'rejeita valor vazio',
    cep: '81234567',
    valor: '',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
  {
    nome: 'rejeita valor igual a zero',
    cep: '81234567',
    valor: '0',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
  {
    nome: 'rejeita valor negativo',
    cep: '81234567',
    valor: '-10,00',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
  {
    nome: 'rejeita valor com mais de duas casas decimais',
    cep: '81234567',
    valor: '100,999',
    mensagem: 'Dados inválidos',
    role: 'alert',
  },
];

for (const caso of casosFrete) {
  test(`frete — ${caso.nome}`, async ({ page }) => {
    await page.goto('/frete');

    await page.getByLabel('CEP').fill(caso.cep);
    await page.getByLabel('Valor do pedido').fill(caso.valor);
    await page.getByRole('button', { name: 'Calcular frete' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.role);
  });
}
