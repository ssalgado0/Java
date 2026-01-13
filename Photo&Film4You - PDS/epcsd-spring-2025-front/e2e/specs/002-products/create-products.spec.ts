import {expect, testAdmin, testAnonymous, testUser} from '@e2e/fixtures/auth';
import {randomUUID} from 'node:crypto';

testAnonymous('anonymous user should not see create product button', async ({page}, testInfo) => {
  await page.goto('/products');
  const createProductButton = page.getByTestId('create-product-button');

  await expect(createProductButton).toBeHidden();

  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();
  await page.goto('/products/create');
  await page.waitForURL(expectedHome);
});

testUser('regular user should not see create product button', async ({page}, testInfo) => {
  await page.goto('/products');
  const createProductButton = page.getByTestId('create-product-button');

  await expect(createProductButton).toBeHidden();

  const baseURL = testInfo.project.use.baseURL as string;
  const expectedHome = new URL('/', baseURL).toString();
  await page.goto('/products/create');
  await page.waitForURL(expectedHome);
});

testAdmin('admin should see create product button', async ({page}) => {
  await page.goto('/products');
  const createProductButton = page.getByTestId('create-product-button');

  await expect(createProductButton).toBeVisible();

  // Click create product button
  await Promise.all([
    createProductButton.click(),
    page.waitForURL(url => url.toString().endsWith('/products/create'))
  ]);
});

testAdmin('admin should be able to create new products', async ({page}) => {
  // Wait for categories request
  await Promise.all([
    page.goto('/products/create'),
    page.waitForResponse((r) =>
      r.url().includes('categories') &&
      r.request().method() === 'GET'
    )
  ]);

  const nameInput = page.getByTestId('product-name-input');
  const descriptionInput = page.getByTestId('product-description-input');
  const priceInput = page.getByTestId('product-price-input');
  const brandInput = page.getByTestId('product-brand-input');
  const modelInput = page.getByTestId('product-model-input');
  const categorySelect = page.getByTestId('product-category-select');
  const createProductButton = page.getByTestId('create-product-button');

  await expect(nameInput).toBeVisible();
  await expect(descriptionInput).toBeVisible();
  await expect(priceInput).toBeVisible();
  await expect(brandInput).toBeVisible();
  await expect(modelInput).toBeVisible();
  await expect(categorySelect).toBeVisible();
  await expect(createProductButton).toBeVisible();
  await expect(createProductButton).toBeDisabled();

  // Fill form
  const newProductName = `1 Test Product - ${randomUUID()}`;
  await nameInput.fill(newProductName);
  await descriptionInput.fill('Test Description');
  await priceInput.fill('100');
  await brandInput.fill('Test Brand');
  await modelInput.fill('Test Model');

  // Select category
  await categorySelect.click();
  const firstCategoryOption = page.getByTestId('product-category-option').first();
  await expect(firstCategoryOption).toBeVisible();
  await firstCategoryOption.click();

  // Click create product button
  await expect(createProductButton).toBeEnabled();

  const [createResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().endsWith('/api/products') &&
      r.request().method() === 'POST'
    ),
    createProductButton.click(),
    page.waitForURL(url => url.toString().endsWith('/products'))
  ]);
  expect(createResponse.status()).toBe(201);

  // Assert product is created
  const newProductCard = page.getByTestId('product-card').filter({hasText: newProductName});
  await expect(newProductCard).toBeVisible();
  await expect(newProductCard.getByTestId('product-name')).toHaveText(newProductName);
});
