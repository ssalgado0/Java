import {expect, testAdmin, testAnonymous, testUser} from '@e2e/fixtures/auth';

testAnonymous('anonymous user should not see sessions', async ({page}, testInfo) => {
  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();

  await page.goto('/sessions');

  const snack = page.locator([
    'mat-snack-bar-container.snackbar-error',
    'mat-mdc-snack-bar-container.snackbar-error',
    '.cdk-overlay-container .snackbar-error',
  ].join(', '));

  await page.waitForURL(expectedHome);

  await expect(snack).toBeVisible({timeout: 10_000});
  await expect(snack).toContainText('No tienes permisos para acceder a esta sección');
});

testUser('regular user should see its sessions', async ({page}) => {
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // First card visible
  const firstSession = page.getByTestId('session-card').first();
  await expect(firstSession).toBeVisible();

  // Card fields visible except email
  const sessionName = firstSession.getByTestId('session-name');
  const sessionEmail = firstSession.getByTestId('session-email');
  const sessionStatus = firstSession.getByTestId('session-status');

  await expect(sessionName).toBeVisible();
  await expect(sessionEmail).toBeHidden();
  await expect(sessionStatus).toBeVisible();
});

testUser('regular user should not see all sessions button', async ({page}) => {
  await page.goto('/sessions');
  const showAllSessionsButton = page.getByTestId('toggle-view-all-sessions-button');
  await expect(showAllSessionsButton).toBeHidden();
})

testAdmin('admin should see all sessions', async ({page}) => {
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Count user's sessions
  const privateSessionsCount = await page.getByTestId('session-card').count();

  // Click show all sessions button
  const showAllSessionsButton = page.getByTestId('toggle-view-all-sessions-button');
  await expect(showAllSessionsButton).toBeVisible();
  await Promise.all([
    showAllSessionsButton.click(),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital/allDigital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Count all sessions
  const allSessionsCount = await page.getByTestId('session-card').count();
  expect(allSessionsCount).toBeGreaterThan(privateSessionsCount);

  // First card visible
  const firstSession = page.getByTestId('session-card').first();
  await expect(firstSession).toBeVisible();

  // Card fields visible
  const sessionName = firstSession.getByTestId('session-name');
  const sessionEmail = firstSession.getByTestId('session-email');
  const sessionStatus = firstSession.getByTestId('session-status');

  await expect(sessionName).toBeVisible();
  await expect(sessionEmail).toBeVisible();
  await expect(sessionStatus).toBeVisible();
});
