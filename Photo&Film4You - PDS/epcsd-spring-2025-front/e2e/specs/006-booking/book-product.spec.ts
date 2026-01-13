import {expect, testUser as test} from '@e2e/fixtures/auth';
import {DatePickerComponent} from '@e2e/pages/components/date-picker.component';

test('user should be able to book some products', async ({page}) => {
  await page.goto('/products');

  // Select date range
  const startDate = new Date();
  startDate.setDate(1);
  startDate.setMonth(startDate.getMonth() + 5);
  const endDate = new Date(startDate);
  endDate.setDate(endDate.getDate() + 5);

  const datePicker = new DatePickerComponent(page);
  await datePicker.selectDateRange(startDate, endDate);

  // Ensure products are visible
  const firstProductCard = page.getByTestId('product-card').filter({hasText: 'Canon EOS R5 C'}).first();
  const secondProductCard = page.getByTestId('product-card').filter({hasText: 'Canon EOS R8'}).first();
  await expect(firstProductCard).toBeVisible();
  await expect(secondProductCard).toBeVisible();

  // Add products to cart
  await firstProductCard.getByTestId('add-product-button').click();
  await secondProductCard.getByTestId('add-product-button').click();

  // Click cart button
  const cartButton = page.getByTestId('cart-button');
  await expect(cartButton).toBeVisible();
  await Promise.all([
    page.waitForURL(url => url.toString().endsWith('/cart')),
    cartButton.click()
  ]);

  // Ensure two cart rows added
  const cartSummaryTable = page.getByTestId('cart-summary-table');
  await expect(cartSummaryTable).toBeVisible();

  const cartRowsCount = await cartSummaryTable.getByTestId('cart-row').count();
  expect(cartRowsCount).toBe(2);

  // Click checkout button
  const checkoutButton = page.getByTestId('checkout-button');
  await expect(checkoutButton).toBeVisible();
  await expect(checkoutButton).toBeEnabled();

  const [createBookingResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/bookings') &&
      r.request().method() === 'POST'
    ),
    checkoutButton.click()
  ]);
  expect(createBookingResponse.status()).toBe(201);

  // Assert booking review page
  const [bookingDetailsResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().includes('/api/bookings/') &&
      r.request().method() === 'GET'
    ),
    page.waitForURL(url => url.toString().includes('/bookings/')),
  ]);
  expect(bookingDetailsResponse.status()).toBe(200);

  // Count booking details rows
  const bookingDetailsRowsCount = await page.getByTestId('booking-detail-row').count();
  expect(bookingDetailsRowsCount).toBe(2);
});
