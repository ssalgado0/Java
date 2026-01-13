import {expect, test} from '@playwright/test';
import {login} from '@e2e/fixtures/login';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

const accountToEdit: LoginRequest = {
  email: 'vidal_c@gmail.com',
  password: 'admin'
}

test.describe('Editar perfil', () => {

  test('permite guardar cambios de perfil con datos válidos', async ({ page }) => {
    await login(page, accountToEdit);

    await page.goto('/profile/edit');

    await expect(
      page.getByRole('heading', { name: /editar perfil/i })
    ).toBeVisible();

    const fullName   = page.locator('input[formcontrolname="fullName"]');
    const phoneInput = page.locator('input[formcontrolname="phoneNumber"]');

    await fullName.fill('Usuario Playwright');
    await phoneInput.fill('987654321');

    await page.getByRole('button', { name: /guardar cambios/i }).click();

    await expect(page).toHaveURL(/\/profile\b/);
  });

  test('muestra error si el teléfono contiene letras', async ({ page }) => {
    await login(page, accountToEdit);

    await page.goto('/profile/edit');

    await expect(
      page.getByRole('heading', { name: /editar perfil/i })
    ).toBeVisible();

    const fullName   = page.locator('input[formcontrolname="fullName"]');
    const phoneInput = page.locator('input[formcontrolname="phoneNumber"]');

    await fullName.fill('Usuario Test');

    await phoneInput.fill('abc123');

    await fullName.click();

    await expect(
      page.getByText('Solo se permiten números')
    ).toBeVisible();

    await expect(
      page.getByRole('button', { name: /guardar cambios/i })
    ).toBeDisabled();
  });

});
