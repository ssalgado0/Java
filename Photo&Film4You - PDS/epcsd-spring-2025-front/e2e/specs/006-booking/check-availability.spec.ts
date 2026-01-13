import {expect, testAnonymous as test} from '@e2e/fixtures/auth';
import {DatePickerComponent} from '@e2e/pages/components/date-picker.component';

test('users should be able to check availability', async ({page}) => {
  await page.goto('/products');

  // Ensure no availability is displayed
  await expect(page.getByTestId('product-availability').first()).toBeHidden();

  // Date picker panel is visible
  const datePicker = new DatePickerComponent(page);
  await expect(datePicker.datePickerPanel).toBeVisible();

  // Fill date range and apply
  await expect(datePicker.applyDateRangeButton).toBeDisabled();

  const startDate = new Date();
  startDate.setDate(startDate.getDate() + 1);
  const endDate = new Date(startDate);
  endDate.setDate(startDate.getDate() + 5);
  await datePicker.fillDateRange(startDate, endDate);
  await expect(datePicker.applyDateRangeButton).toBeEnabled();

  const [availabilityResponse] = await datePicker.applyFilter();
  expect(availabilityResponse.status()).toBe(200);

  // Ensure availability is displayed
  await expect(page.getByTestId('product-availability').first()).toBeVisible();
});
