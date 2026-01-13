import {expect, test} from '@e2e/coverage.config';
import {credentials} from '@e2e/support/credentials';

test('open login dialog, authenticate user and updates navbar', async ({page}) => {
  await page.goto('/');

  // Open login dialog
  const navbar = page.getByTestId('navbar');
  await navbar.getByTestId('login-button').click();
  await expect(page.getByTestId('login-form')).toBeVisible();

  // Fill dialog
  const creds = credentials.USER;
  const loginForm = page.getByTestId('login-form');
  await loginForm.getByTestId('email-input').fill(creds.email);
  await loginForm.getByTestId('password-input').fill(creds.password);

  // Submit
  const [loginResponse] = await Promise.all([
    page.waitForResponse(r =>
      r.request().method() === 'POST' &&
      r.url().endsWith('/api/auth/login')
    ),
    loginForm.getByTestId('login-submit-button').click(),
  ]);

  expect(loginResponse.status()).toBe(200);

  // Dialog is closed and navbar is updated
  await expect(loginForm).toBeHidden();
  await expect(navbar.getByTestId('login-button')).toBeHidden();
  await expect(navbar.getByTestId('avatar-button')).toBeVisible();
});

test('login with incorrect credentials shows error snackbar', async ({page}) => {
  await page.goto('/');

  // Open login dialog
  const navbar = page.getByTestId('navbar');
  await navbar.getByTestId('login-button').click();
  await expect(page.getByTestId('login-form')).toBeVisible();

  // Fill dialog
  const loginForm = page.getByTestId('login-form');
  await loginForm.getByTestId('email-input').fill('incorrectemail@test.com');
  await loginForm.getByTestId('password-input').fill('incorrectpassword');

  // Submit
  const [loginResponse] = await Promise.all([
    page.waitForResponse(r =>
      r.request().method() === 'POST' &&
      r.url().endsWith('/api/auth/login')
    ),
    loginForm.getByTestId('login-submit-button').click(),
  ]);
  expect(loginResponse.status()).toBe(403);

  // Dialog is not closed, navbar is not updated and snackbar is shown
  await expect(loginForm).toBeVisible();
  await expect(navbar.getByTestId('login-button')).toBeVisible();
  await expect(navbar.getByTestId('avatar-button')).toBeHidden();
  const snack = page.locator([
    'mat-snack-bar-container.snackbar-error',
    'mat-mdc-snack-bar-container.snackbar-error',
    '.cdk-overlay-container .snackbar-error',
  ].join(', '));

  await expect(snack).toBeVisible({timeout: 10_000});
  await expect(snack).toContainText('Credenciales incorrectas');
});

test('close login dialog', async ({page}) => {
  await page.goto('/');

  // Open login dialog
  const navbar = page.getByTestId('navbar');
  await navbar.getByTestId('login-button').click();
  await expect(page.getByTestId('login-form')).toBeVisible();

  // Close dialog
  await page.getByTestId('login-cancel-button').click();
  await expect(page.getByTestId('login-form')).toBeHidden();
});
