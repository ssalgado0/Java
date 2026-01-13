import {expect, testAnonymous, testUser} from '@e2e/fixtures/auth';

testUser('logout from navbar menu', async ({page}, testInfo) => {
  await page.goto('/');

  // Precondition
  const navbar = page.getByTestId('navbar');
  await expect(navbar.getByTestId('avatar-button')).toBeVisible();

  // Open user menu
  await navbar.getByTestId('avatar-button').click();

  // Click logout button
  const logoutBtn = page.getByTestId('logout-button');
  await expect(logoutBtn).toBeVisible();

  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();
  await Promise.all([
    page.waitForURL(expectedHome),
    logoutBtn.click()
  ]);

  // Navbar updated
  await expect(navbar.getByTestId('avatar-button')).toBeHidden();
  await expect(navbar.getByTestId('login-button')).toBeVisible();

  // Token removed from sessionstorage
  await expect
    .poll(async () => page.evaluate(() => sessionStorage.getItem('jwt_token')), {
      timeout: 5000,
    })
    .toBeNull();
});

testAnonymous('anonymous user cannot logout', async ({page}) => {
  await page.goto('/');
  const navbar = page.getByTestId('navbar');
  await expect(navbar.getByTestId('avatar-button')).toBeHidden();
  await expect(navbar.getByTestId('login-button')).toBeVisible();
});
