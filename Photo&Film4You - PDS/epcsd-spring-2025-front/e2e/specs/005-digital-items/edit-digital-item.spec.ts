import {expect, testAdmin as test} from '@e2e/fixtures/auth';
import {randomUUID} from 'node:crypto';

test('admin should be able to edit digital items from a digital session', async ({page}) => {
  // Go to digital session details
  await Promise.all([
    page.goto('/sessions/1'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/digitalItemBySession?digitalSessionId=1') &&
      r.request().method() === 'GET'
    )
  ]);

  const itemRow = page.getByTestId('digital-item-row').first();
  await expect(itemRow).toBeVisible();

  const itemId = (await itemRow.getByTestId('digital-item-id').textContent())!.trim();
  const editButton = itemRow.getByTestId('digital-item-edit-button');
  await expect(editButton).toBeVisible();

  // Click edit button
  await Promise.all([
    editButton.click(),
    page.waitForURL(url => url.toString().endsWith(`/sessions/1/items/${itemId}/edit`)),
    page.waitForResponse((r) =>
      r.url().endsWith(`/api/digitalItem/${itemId}`) &&
      r.request().method() === 'GET'
    )
  ]);

  // Assert item form is visible
  const itemForm = page.getByTestId('digital-item-form');
  await expect(itemForm).toBeVisible();
  const submitButton = itemForm.getByTestId('submit-digital-item-button');
  await expect(submitButton).toBeVisible();
  await expect(submitButton).toBeEnabled();

  // Update description
  const newDescription = `New description - ${randomUUID()}`;
  const descriptionInput = itemForm.getByTestId('description-input');
  await descriptionInput.clear();
  await descriptionInput.fill(newDescription);


  // Submit form
  const [updateResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith(`/api/digitalItem/updateItem/${itemId}`) &&
      r.request().method() === 'PUT'
    ),
    submitButton.click()
  ]);
  expect(updateResponse.status()).toBe(200);

  // Refresh digital items
  await Promise.all([
    page.waitForURL(url => url.toString().endsWith('/sessions/1')),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/digitalItemBySession?digitalSessionId=1') &&
      r.request().method() === 'GET'
    )
  ]);

  // Get updated item row
  const editedRow = page.getByTestId('digital-item-row').filter({hasText: newDescription}).first();
  await expect(editedRow).toBeVisible();

  const editedItemId = (await editedRow.getByTestId('digital-item-id').textContent())!.trim();
  expect(editedItemId).toBe(itemId);
});
