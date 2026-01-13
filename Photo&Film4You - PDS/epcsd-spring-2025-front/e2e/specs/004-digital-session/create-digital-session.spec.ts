import {expect, testAdmin, testUser} from '@e2e/fixtures/auth';
import {randomUUID} from 'node:crypto';

testUser('regular user should see create session button', async ({page}) => {
  // Assert create session button exists
  await page.goto('/sessions');
  const createSessionButton = page.getByTestId('create-session-button');
  await expect(createSessionButton).toBeVisible();

  // Click create session button
  await Promise.all([
    createSessionButton.click(),
    page.waitForURL(url => url.toString().endsWith('/sessions/create'))
  ]);

  // Assert session form is visible
  const sessionForm = page.getByTestId('session-form');
  await expect(sessionForm).toBeVisible();

  // Assert session form fields are visible except email
  const emailInput = sessionForm.getByTestId('email-input');
  const descriptionInput = sessionForm.getByTestId('description-input');
  const submitButton = sessionForm.getByTestId('submit-session-button');

  await expect(emailInput).toBeHidden();
  await expect(descriptionInput).toBeVisible();
  await expect(submitButton).toBeVisible();
  await expect(submitButton).toBeDisabled();

  // Fill form
  const sessionDescription = `Test Session - ${randomUUID()}`;
  await descriptionInput.fill(sessionDescription);
  await expect(submitButton).toBeEnabled();

  // Click submit button and refresh sessions
  const [createResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital/createDigital') &&
      r.request().method() === 'POST'
    ),
    submitButton.click()
  ]);
  expect(createResponse.status()).toBe(201);

  // Assert session is created
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);
  const newSessionCard = page.getByTestId('session-card').filter({hasText: sessionDescription}).first();
  await expect(newSessionCard).toBeVisible();
  await expect(newSessionCard.getByTestId('session-name')).toHaveText(sessionDescription);
});

testAdmin('admin user should see create session button', async ({page}) => {
  // Assert create session button exists
  await page.goto('/sessions');
  const createSessionButton = page.getByTestId('create-session-button');
  await expect(createSessionButton).toBeVisible();

  // Click create session button
  await Promise.all([
    createSessionButton.click(),
    page.waitForURL(url => url.toString().endsWith('/sessions/create'))
  ]);

  // Assert session form is visible
  const sessionForm = page.getByTestId('session-form');
  await expect(sessionForm).toBeVisible();

  // Assert session form fields are visible except email
  const emailInput = sessionForm.getByTestId('email-input');
  const descriptionInput = sessionForm.getByTestId('description-input');
  const submitButton = sessionForm.getByTestId('submit-session-button');

  await expect(emailInput).toBeVisible();
  await expect(descriptionInput).toBeVisible();
  await expect(submitButton).toBeVisible();
  await expect(submitButton).toBeDisabled();

  // Fill form
  const sessionDescription = `Test Session - ${randomUUID()}`;
  await descriptionInput.fill(sessionDescription);
  await expect(submitButton).toBeEnabled();

  // Click submit button and refresh sessions
  const [createResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital/createDigital') &&
      r.request().method() === 'POST'
    ),
    submitButton.click()
  ]);
  expect(createResponse.status()).toBe(201);

  // Assert session is created
  await Promise.all([
    page.goto('/sessions'),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/digital') &&
      r.request().method() === 'GET'
    )
  ]);
  const newSessionCard = page.getByTestId('session-card').filter({hasText: sessionDescription}).first();
  await expect(newSessionCard).toBeVisible();
  await expect(newSessionCard.getByTestId('session-name')).toHaveText(sessionDescription);
});
