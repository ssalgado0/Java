import {expect, testAdmin as test} from '@e2e/fixtures/auth';
import {randomUUID} from 'node:crypto';

test('admin should be able to add digital items into a digital session', async ({page}) => {
  // Go to digital session details
  await Promise.all([
    page.goto('/sessions/1'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/digitalItemBySession?digitalSessionId=1') &&
      r.request().method() === 'GET'
    )
  ]);

  // Count items before adding a new one
  const rowsCountBeforeAdding = await page.getByTestId('digital-item-row').count();

  // Click create item button
  const addButton = page.getByTestId('create-digital-item-button');
  await expect(addButton).toBeVisible();

  await Promise.all([
    addButton.click(),
    page.waitForURL(url => url.toString().endsWith('/sessions/1/items/create'))
  ]);

  // Assert item form is visible
  const itemForm = page.getByTestId('digital-item-form');
  await expect(itemForm).toBeVisible();
  const submitButton = itemForm.getByTestId('submit-digital-item-button');
  await expect(submitButton).toBeVisible();
  await expect(submitButton).toBeDisabled();


  // Fill form
  const description = `Test item - ${randomUUID()}`;
  const descriptionInput = itemForm.getByTestId('description-input');
  const latInput = itemForm.getByTestId('lat-input');
  const lonInput = itemForm.getByTestId('lon-input');
  const linkInput = itemForm.getByTestId('link-input');

  await descriptionInput.fill(description);
  await latInput.fill('10.0');
  await lonInput.fill('10.0');
  await linkInput.fill('https://www.google.com');
  await expect(submitButton).toBeEnabled();

  // Submit form
  const [createResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/addItem') &&
      r.request().method() === 'POST'
    ),
    submitButton.click()
  ]);
  expect(createResponse.status()).toBe(201);

  // Wait for refresh request
  await Promise.all([
    page.waitForURL(url => url.toString().endsWith('/sessions/1')),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/digitalItemBySession?digitalSessionId=1') &&
      r.request().method() === 'GET'
    )
  ]);

  // Assert new item is added
  const rowsCountAfterAdding = await page.getByTestId('digital-item-row').count();
  expect(rowsCountAfterAdding).toBeGreaterThan(rowsCountBeforeAdding);
  const newRow = page.getByTestId('digital-item-row').last();
  await expect(newRow).toContainText(description);
});
