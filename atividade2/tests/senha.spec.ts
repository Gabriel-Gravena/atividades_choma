import { test, expect } from '@playwright/test';

const casosSenha = [
  {
    nome: 'aceita senha no limite mínimo de 8 caracteres',
    senha: 'Abcdef1!',
    confirmacao: 'Abcdef1!',
    mensagem: 'Senha cadastrada',
    role: 'status',
    sucesso: true,
  },
  {
    nome: 'aceita senha dentro do intervalo permitido',
    senha: 'Abcdefg1!',
    confirmacao: 'Abcdefg1!',
    mensagem: 'Senha cadastrada',
    role: 'status',
    sucesso: true,
  },
  {
    nome: 'aceita senha no limite máximo de 20 caracteres',
    senha: 'Abcdefghijklmnopqr1!',
    confirmacao: 'Abcdefghijklmnopqr1!',
    mensagem: 'Senha cadastrada',
    role: 'status',
    sucesso: true,
  },
  {
    nome: 'rejeita senha abaixo do mínimo de caracteres',
    senha: 'Abcde1!',
    confirmacao: 'Abcde1!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha acima do máximo de caracteres',
    senha: 'Abcdefghijklmnopqrs1!',
    confirmacao: 'Abcdefghijklmnopqrs1!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha sem letra maiúscula',
    senha: 'abcdefg1!',
    confirmacao: 'abcdefg1!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha sem letra minúscula',
    senha: 'ABCDEFG1!',
    confirmacao: 'ABCDEFG1!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha sem número',
    senha: 'Abcdefgh!',
    confirmacao: 'Abcdefgh!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha que contém espaço',
    senha: 'Abc def1!',
    confirmacao: 'Abc def1!',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita senha vazia',
    senha: '',
    confirmacao: '',
    mensagem: 'Senha fora do padrão',
    role: 'alert',
    sucesso: false,
  },
  {
    nome: 'rejeita confirmação diferente para senha válida',
    senha: 'Abcdef1!',
    confirmacao: 'Abcdef2!',
    mensagem: 'As senhas não coincidem',
    role: 'alert',
    sucesso: false,
  },
];

for (const caso of casosSenha) {
  test(`senha — ${caso.nome}`, async ({ page }) => {
    await page.goto('/senha');

    await page.getByLabel('Nova senha').fill(caso.senha);
    await page.getByLabel('Confirmar senha').fill(caso.confirmacao);
    await page.getByRole('button', { name: 'Cadastrar senha' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.role);

    if (caso.sucesso) {
      await expect(page.getByLabel('Nova senha')).toHaveValue('');
      await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
    }
  });
}
