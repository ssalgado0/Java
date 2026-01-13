import {expect, testAdmin as test} from '@e2e/fixtures/auth';

test('admin should be able to see digital items from a digital session', async ({page}) => {
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

  await expect(itemRow.getByTestId('digital-item-id')).toBeVisible();
  await expect(itemRow.getByTestId('digital-item-description')).toBeVisible();
  await expect(itemRow.getByTestId('digital-item-lat')).toBeVisible();
  await expect(itemRow.getByTestId('digital-item-lon')).toBeVisible();
  await expect(itemRow.getByTestId('digital-item-link')).toBeVisible();
  await expect(itemRow.getByTestId('digital-item-status')).toBeVisible();
});
