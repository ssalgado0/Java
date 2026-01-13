import {expect, testAdmin as test} from '@e2e/fixtures/auth';
import {ConfirmDialogComponent} from '@e2e/pages/components/confirm-dialog.component';

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

  const description = (await itemRow.getByTestId('digital-item-description').textContent())!.trim();
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

  // Click delete button
  const deleteButton = itemForm.getByTestId('delete-digital-item-button');
  await expect(deleteButton).toBeVisible();

  await deleteButton.click();

  // Assert confirm dialog open
  const confirmDialog = new ConfirmDialogComponent(page);
  await confirmDialog.expectDialogTitle('Eliminar elemento digital');

  const [deleteResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith(`/api/digitalItem/dropItem/${itemId}`) &&
      r.request().method() === 'DELETE'
    ),
    confirmDialog.confirm()
  ]);
  expect(deleteResponse.status()).toBe(200);

  // Refresh digital items
  await Promise.all([
    page.waitForURL(url => url.toString().endsWith('/sessions/1')),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digitalItem/digitalItemBySession?digitalSessionId=1') &&
      r.request().method() === 'GET'
    )
  ]);

  // Deleted row should not appear
  const deletedRow = page.getByTestId('digital-item-row').filter({hasText: description}).first();
  await expect(deletedRow).toBeHidden();
});
