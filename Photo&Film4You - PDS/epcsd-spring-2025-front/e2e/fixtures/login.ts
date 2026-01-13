import {expect, Page} from '@playwright/test';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

export async function login(page: Page, credentials: LoginRequest) {
  await page.goto('/');

  await page.getByRole('button', {name: /iniciar sesión/i}).click();

  await page.getByLabel('Email').fill(credentials.email);
  await page.getByLabel('Contraseña').fill(credentials.password);

  await page.getByRole('button', {name: /entrar/i}).click();

  await expect(
    page.getByRole('button', {name: /productos/i})
  ).toBeVisible();
}
