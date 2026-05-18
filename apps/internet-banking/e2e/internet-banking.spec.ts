import { expect, test } from '@playwright/test';

test('fluxo principal do internet banking', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Entrar na conta' })).toBeVisible();
  await page.getByLabel('E-mail').fill('joao@bancopagamento.com');
  await page.getByRole('textbox', { name: 'Senha' }).fill('Senha@123');
  await page.getByRole('button', { name: /entrar/i }).click();

  await expect(page).toHaveURL(/\/extrato/);
  await expect(page.getByRole('heading', { name: 'Extrato de Conta' })).toBeVisible();

  await page.getByLabel('Filtrar por tipo de transação').click();
  await page.getByRole('option', { name: 'Créditos' }).click();
  await expect(page.getByText('Salario')).toBeVisible();

  await page.getByRole('menuitem', { name: /pagar boleto/i }).click();
  await expect(page).toHaveURL(/\/pagamento/);
  await page.getByLabel('Código de barras ou linha digitável').fill('00190000000000000000000000000000000000000000123');
  await page.getByRole('button', { name: /consultar boleto/i }).click();

  await expect(page.getByText('Agua e Saneamento Municipal')).toBeVisible();
  await expect(page.getByText('Dados do Boleto')).toBeVisible();

  await page.getByRole('button', { name: 'Abrir menu do usuário' }).click();
  await page.getByRole('menuitem', { name: 'Sair' }).click();

  await expect(page).toHaveURL(/\/login/);
  await expect(page.getByRole('heading', { name: 'Entrar na conta' })).toBeVisible();
});
