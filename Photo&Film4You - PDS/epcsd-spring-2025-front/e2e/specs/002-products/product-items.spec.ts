import {expect, testAdmin, testAnonymous, testUser} from '@e2e/fixtures/auth';
import {randomUUID} from 'node:crypto';

testAnonymous('anonymous user should not see items', async ({page}, testInfo) => {
  await page.goto('/products');
  const card = page.getByTestId('product-card').first();
  await expect(card.getByTestId('product-items-button')).toBeHidden();

  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();
  await page.goto('/products/1/items');
  await page.waitForURL(expectedHome);
});

testUser('regular user should not see items', async ({page}, testInfo) => {
  await page.goto('/products');
  const card = page.getByTestId('product-card').first();
  await expect(card.getByTestId('product-items-button')).toBeHidden();

  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();
  await page.goto('/products/1/items');
  await page.waitForURL(expectedHome);
});

testAdmin('admin should see items', async ({page}) => {
  await page.goto('/products');
  const card = page.getByTestId('product-card').first();
  const itemsButton = card.getByTestId('product-items-button');
  await expect(itemsButton).toBeVisible();

  // Click items button
  const [itemsResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().includes('/api/items/product/') &&
      r.request().method() === 'GET'
    ),
    itemsButton.click(),
    page.waitForURL(url => url.toString().endsWith('/items'))
  ]);
  expect(itemsResponse.status()).toBe(200);
});

testAdmin('admin should be able to change item status', async ({page}) => {
  await page.goto('/products/1/items');

  const itemRow = page.getByTestId('item-row').first();
  await expect(itemRow).toBeVisible();

  const serialNumber = itemRow.getByTestId('item-serial');
  const itemStatus = itemRow.getByTestId('item-status');
  const toggleStatusButton = itemRow.getByTestId('toggle-item-status-button');

  await expect(serialNumber).toBeVisible();
  await expect(itemStatus).toBeVisible();
  await expect(toggleStatusButton).toBeVisible();

  const serial = await serialNumber.textContent();
  const textBeforeChange = await itemStatus.textContent();

  // Click change items status button
  const [patchResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith(`/api/items/${serial?.trim()}`) &&
      r.request().method() === 'PATCH'
    ),
    toggleStatusButton.click()
  ]);
  expect(patchResponse.status()).toBe(200);

  // Item status should have changed
  const updatedRow = page.getByTestId('item-row').filter({hasText: serial!}).first();
  const textAfterChange = await updatedRow.getByTestId('item-status').textContent();
  expect(textAfterChange).not.toEqual(textBeforeChange);
});

testAdmin('admin should be able to add items', async ({page}) => {
  await page.goto('/products/1/items');

  const rowsCountBeforeAdding = await page.getByTestId('item-row').count();

  const newSerialInput = page.getByTestId('new-item-serial-input');
  const addButton = page.getByTestId('add-item-button');

  await expect(newSerialInput).toBeVisible();
  await expect(addButton).toBeVisible();

  // Fill new serial input
  const newSerial = `SN-${randomUUID()}`;
  await newSerialInput.fill(newSerial);

  // Click add button
  const [crateResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith(`/api/items`) &&
      r.request().method() === 'POST'
    ),
    addButton.click()
  ]);
  expect(crateResponse.status()).toBe(201);

  // Wait for refresh request
  await page.waitForResponse((r) =>
    r.url().endsWith(`/api/items/product/1`) &&
    r.request().method() === 'GET'
  );

  // New item should be added
  const rowsCountAfterAdding = await page.getByTestId('item-row').count();
  expect(rowsCountAfterAdding).toBeGreaterThan(rowsCountBeforeAdding);
  const newRow = page.getByTestId('item-row').filter({hasText: newSerial}).first();
  await expect(newRow).toContainText(newSerial);
});
