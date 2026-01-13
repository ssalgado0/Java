import {expect, test} from '@playwright/test';
import {login} from '@e2e/fixtures/login';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

const accountToDelete: LoginRequest = {
  email: 'jmgarcia@terra.es',
  password: '12345'
}

test.describe('Eliminar cuenta', () => {

  test('deshabilita el botón si no se escribe ELIMINAR correctamente', async ({ page }) => {
    await login(page, accountToDelete);

    await page.goto('/profile/delete');

    const input = page.getByRole('textbox', { name: /confirmación/i });
    await expect(input).toBeVisible();

    await input.fill('ELIMINA');

    const deleteButton = page.getByRole('button', { name: /eliminar cuenta/i });

    await expect(deleteButton).toBeDisabled();

    await expect(page).toHaveURL(/\/profile\/delete$/);
  });

  test('elimina la cuenta cuando se escribe ELIMINAR', async ({ page }) => {
    await login(page, accountToDelete);

    await page.goto('/profile/delete');

    const input = page.getByRole('textbox', { name: /confirmación/i });
    await expect(input).toBeVisible();

    await input.fill('ELIMINAR');

    const deleteButton = page.getByRole('button', { name: /eliminar cuenta/i });
    await expect(deleteButton).toBeEnabled();

    await deleteButton.click();

    await expect(page).toHaveURL(/\/$/);
  });

});
