import {expect, testAdmin, testAnonymous} from '@e2e/fixtures/auth';

testAnonymous('anonymous user should not be able to review bookings', async ({page}, testInfo) => {
  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();

  await page.goto('/bookings');

  const snack = page.locator([
    'mat-snack-bar-container.snackbar-error',
    'mat-mdc-snack-bar-container.snackbar-error',
    '.cdk-overlay-container .snackbar-error',
  ].join(', '));

  await page.waitForURL(expectedHome);

  await expect(snack).toBeVisible({timeout: 10_000});
  await expect(snack).toContainText('No tienes permisos para acceder a esta sección');
});

testAdmin('logged user should be able to review its bookings', async ({page}) => {
  // Go to bookings review
  const [bookingsResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/bookings') &&
      r.request().method() === 'GET'
    ),
    page.goto('/bookings')
  ]);
  expect(bookingsResponse.status()).toBe(200);

  // Assert there are rows in the table
  const bookingRows = page.getByTestId('booking-row');
  const bookingRowCount = await bookingRows.count();
  await expect(bookingRows.first()).toBeVisible();
  expect(bookingRowCount).toBeGreaterThan(0);
});

testAdmin('logged user should be able to expand and check booking details', async ({page}) => {
  // Go to bookings review
  const [bookingsResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/bookings') &&
      r.request().method() === 'GET'
    ),
    page.goto('/bookings')
  ]);
  expect(bookingsResponse.status()).toBe(200);

  const firstBookingRow = page.getByTestId('booking-row').first();
  await expect(firstBookingRow).toBeVisible();

  // Click row and assert details is expanded
  const expandDetails = firstBookingRow.getByTestId('booking-expand-details-button');
  await expandDetails.click();
  const expandedDetail = page.getByTestId('booking-expanded-detail').first();
  await expect(expandedDetail).toBeVisible();
});
