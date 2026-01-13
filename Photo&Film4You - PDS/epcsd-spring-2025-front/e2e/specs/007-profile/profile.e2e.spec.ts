import {expect, test} from '@playwright/test';
import {login} from '@e2e/fixtures/login';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

const account: LoginRequest = {
  email: 'vidal_c@gmail.com',
  password: 'admin'
}

test.describe('Perfil de usuario', () => {

  test('muestra la página de perfil del usuario', async ({ page }) => {
    await login(page, account);

    await page.goto('/profile');

    await expect(
      page.getByRole('heading', { name: /perfil/i })
    ).toBeVisible();

    await expect(
      page.getByText(/teléfono/i)
    ).toBeVisible();
  });

});
