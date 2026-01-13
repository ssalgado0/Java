import {expect, Page, test} from '@playwright/test';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

const credentialsForPasswordChange: LoginRequest = {
  email: 'fperez@gmail.com',
  password: '12345'
}

test.describe('Cambio de contraseña', () => {

  async function login(page: Page) {
    await page.goto('/');

    await page.getByRole('button', { name: /iniciar sesión/i }).click();

    await page.getByLabel('Email').fill(credentialsForPasswordChange.email);
    await page.getByLabel('Contraseña').fill(credentialsForPasswordChange.password);

    await page.getByRole('button', { name: /entrar/i }).click();

    await expect(
      page.getByRole('button', { name: /productos/i })
    ).toBeVisible();
  }


  test('muestra error si la contraseña actual es incorrecta', async ({ page }) => {

    await login(page);

    await page.goto('/profile/change-password');

    await expect(
      page.getByRole('heading', { name: /Cambiar contraseña/i })
    ).toBeVisible();

    const current = page.locator('input[formcontrolname="currentPassword"]');
    const nuevo   = page.locator('input[formcontrolname="newPassword"]');
    const confirm = page.locator('input[formcontrolname="confirmNewPassword"]');

    await current.fill('incorrecta');
    await nuevo.fill('admin1238');
    await confirm.fill('admin1238');

    await page.getByRole('button', { name: /cambiar contraseña/i }).click();

    await expect(
      page.getByText('La contraseña actual no es correcta')
    ).toBeVisible();
  });

  test('muestra error si las contraseñas nuevas no coinciden', async ({ page }) => {
    await login(page);
    await page.goto('/profile/change-password');

    await page.locator('input[formcontrolname="currentPassword"]').fill(credentialsForPasswordChange.password);
    await page.locator('input[formcontrolname="newPassword"]').fill('abc12345');
    await page.locator('input[formcontrolname="confirmNewPassword"]').fill('xxx99999');

    await page.getByRole('button', { name: /cambiar contraseña/i }).click();

    await expect(
      page.getByText(/no coinciden/i)
    ).toBeVisible();
  });

});
