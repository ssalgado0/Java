import {expect, testUser as test} from '@e2e/fixtures/auth';

test('logged user should be able to edit digital sessions', async ({page}) => {
  // List sessions
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  // Get first session card
  const firstOldSession = page.getByTestId('session-card').first();
  await expect(firstOldSession).toBeVisible();

  const editSessionButton = firstOldSession.getByTestId('session-edit-button');
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

  // Assert session form fields are visible and filled
  const descriptionInput = sessionForm.getByTestId('description-input');
  await expect(descriptionInput).toBeVisible();
  expect(await descriptionInput.inputValue()).not.toHaveLength(0);

  // Update description
  const newDescription = 'Edited session description';
  await descriptionInput.clear();
  await descriptionInput.fill(newDescription);

  // Submit update form
  const submitButton = sessionForm.getByTestId('submit-session-button');
  const [updateResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().includes('/api/digital/') &&
      r.request().method() === 'PUT'
    ),
    submitButton.click()
  ]);
  expect(updateResponse.status()).toBe(200);

  // Assert session is updated
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);

  const firstEditedSession = page.getByTestId('session-card').filter({hasText: newDescription}).first();
  const editedDescription = await firstEditedSession.getByTestId('session-name').textContent();
  expect(editedDescription).toBe(newDescription);
});
