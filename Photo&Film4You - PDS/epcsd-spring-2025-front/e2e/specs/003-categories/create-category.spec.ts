import {expect, testAdmin as test} from '@e2e/fixtures/auth';

test('admin should be able to create new categories', async ({page}) => {
  await page.goto('/products/create');

  const createCategoryButton = page.getByTestId('create-category-button');
  await expect(createCategoryButton).toBeVisible();
  await createCategoryButton.click();

  // Create category dialog form should be visible
  const createCategoryForm = page.getByTestId('create-category-form');
  await expect(createCategoryForm).toBeVisible();

  const nameInput = createCategoryForm.getByTestId('category-name-input');
  const descriptionInput = createCategoryForm.getByTestId('category-description-input');
  const parentCategorySelect = createCategoryForm.getByTestId('category-parent-category-select');
  const submitButton = createCategoryForm.getByTestId('submit-create-category-button');

  await expect(nameInput).toBeVisible();
  await expect(descriptionInput).toBeVisible();
  await expect(parentCategorySelect).toBeVisible();
  await expect(submitButton).toBeVisible();
  await expect(submitButton).toBeDisabled();

  // Fill form
  const categoryName = 'Test Category';
  await nameInput.fill(categoryName);
  await descriptionInput.fill('Test Description');

  // Click create category button and wait for responses and category select refresh
  await expect(submitButton).toBeEnabled();
  await Promise.all([
    submitButton.click(),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/categories') &&
      r.request().method() === 'POST'
    ),
    page.waitForResponse((r) =>
      r.url().endsWith('/api/categories') &&
      r.request().method() === 'GET'
    )
  ]);

  const categorySelect = page.getByTestId('product-category-select');
  await expect(categorySelect).toBeVisible();
  await categorySelect.click();
  const lastCategoryOption = page.getByTestId('product-category-option').last();
  await expect(lastCategoryOption).toBeVisible();
  await expect(lastCategoryOption).toHaveText(categoryName);
});
