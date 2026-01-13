import {expect, testAdmin as test} from '@e2e/fixtures/auth';
import {ConfirmDialogComponent} from '@e2e/pages/components/confirm-dialog.component';

test('logged user should be able to cancel deleting digital sessions', async ({page}) => {
  // List sessions
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Get first session card
  const firstSession = page.getByTestId('session-card').first();
  await expect(firstSession).toBeVisible();

  const sessionDescription = await firstSession.getByTestId('session-name').textContent();
  const editSessionButton = firstSession.getByTestId('session-edit-button');
  await expect(editSessionButton).toBeVisible();

  // Click edit button
  await Promise.all([
    editSessionButton.click(),
    page.waitForURL(url =>
      url.toString().includes('/sessions/')
      && url.toString().endsWith('/edit')
    ),
    page.waitForResponse((r) =>
      r.url().includes('/api/digital/') &&
      r.request().method() === 'GET'
    )
  ]);

  // Assert session form is visible
  const sessionForm = page.getByTestId('session-form');
  await expect(sessionForm).toBeVisible();

  // Assert delete button is visible and enabled
  const deleteButton = page.getByTestId('delete-session-button');
  await expect(deleteButton).toBeVisible();
  await expect(deleteButton).toBeEnabled();

  await deleteButton.click();

  // Assert confirm dialog open
  const confirmDialog = new ConfirmDialogComponent(page);
  await confirmDialog.expectDialogTitle('Eliminar sesión digital');
  await confirmDialog.cancel();

  // List sessions
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Get edited session
  const editedSession = page.getByTestId('session-card').filter({hasText: sessionDescription!}).first();
  await expect(editedSession).toBeVisible();
});

test('logged user should be able to confirm deleting digital sessions', async ({page}) => {
  // List sessions
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Get first session card
  const firstSession = page.getByTestId('session-card').first();
  await expect(firstSession).toBeVisible();

  const sessionDescription = await firstSession.getByTestId('session-name').textContent();
  const editSessionButton = firstSession.getByTestId('session-edit-button');
  await expect(editSessionButton).toBeVisible();

  // Click edit button
  await Promise.all([
    editSessionButton.click(),
    page.waitForURL(url =>
      url.toString().includes('/sessions/')
      && url.toString().endsWith('/edit')
    ),
    page.waitForResponse((r) =>
      r.url().includes('/api/digital/') &&
      r.request().method() === 'GET'
    )
  ]);

  // Assert session form is visible
  const sessionForm = page.getByTestId('session-form');
  await expect(sessionForm).toBeVisible();

  // Assert delete button is visible and enabled
  const deleteButton = page.getByTestId('delete-session-button');
  await expect(deleteButton).toBeVisible();
  await expect(deleteButton).toBeEnabled();

  await deleteButton.click();

  // Assert confirm dialog open
  const confirmDialog = new ConfirmDialogComponent(page);
  await confirmDialog.expectDialogTitle('Eliminar sesión digital');

  const [deleteResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().includes('/api/digital/removeDigital') &&
      r.request().method() === 'DELETE'
    ),
    confirmDialog.confirm(),
    page.waitForURL(url => url.toString().endsWith('/sessions')),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);
  expect(deleteResponse.status()).toBe(200);

  // Get edited session
  const editedSession = page.getByTestId('session-card').filter({hasText: sessionDescription!}).first();
  await expect(editedSession).toBeHidden();
});
