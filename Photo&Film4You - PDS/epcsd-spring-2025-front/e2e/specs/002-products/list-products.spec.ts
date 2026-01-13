import {expect, test} from '@e2e/coverage.config';

test('list products and show details', async ({page}) => {
  // Go to products
  const [productsResponse] = await Promise.all([
    page.waitForResponse((r) =>
      r.url().includes('/api/products/search') &&
      r.request().method() === 'GET',
      {timeout: 60000}
    ),
    page.goto('/products'),
  ]);
  expect(productsResponse.status()).toBe(200);

  // Assert products are loaded
  const cards = page.getByTestId('product-card');
  expect(await cards.count()).toBeGreaterThan(1);

  // Assert first product
  const firstCard = cards.first();
  const name = firstCard.getByTestId('product-name');
  expect(await name.textContent()).not.toHaveLength(0);

  // Assert product details opened
  const detailsButton = firstCard.getByTestId('product-details-button');
  await expect(detailsButton).toBeVisible();
  await detailsButton.click();

  const productDetailsResponse = await page.waitForResponse((r) =>
    r.url().includes('/api/products/') &&
    r.request().method() === 'GET'
  );
  expect(productDetailsResponse.status()).toBe(200);

  const details = page.getByTestId('product-details-name');
  await expect(details).toBeVisible();
});

test('filter products list', async ({page}) => {
  // Go to products
  await page.goto('/products');

  // Assert products are loaded
  const [productsResponse] = await Promise.all([
    page.waitForResponse((r) => {
      if (r.request().method() !== 'GET') return false;

      const url = new URL(r.url());
      return (
        url.pathname === '/api/products/search' &&
        url.searchParams.get('filterTerm') === '' &&
        url.searchParams.get('page') === '0' &&
        url.searchParams.get('size') === '12' &&
        url.searchParams.get('sort') === 'name,asc'
      );
    }, {timeout: 60000}),
    page.goto('/products'),
  ]);
  expect(productsResponse.status()).toBe(200);

  const nonFilteredCards = page.getByTestId('product-card');
  const nonFilteredCount = await nonFilteredCards.count();
  expect(nonFilteredCount).toBeGreaterThan(1);

  // Filter by name
  const filterInput = page.getByTestId('product-filter-input');
  await filterInput.fill('Canon');
  await expect(filterInput).toHaveValue('Canon');

  const productsFilteredResponse = await page.waitForResponse((r) => {
    if (r.request().method() !== 'GET') return false;

    const url = new URL(r.url());
    return (
      url.pathname === '/api/products/search' &&
      url.searchParams.get('filterTerm') === 'Canon' &&
      url.searchParams.get('page') === '0' &&
      url.searchParams.get('size') === '12' &&
      url.searchParams.get('sort') === 'name,asc'
    );
  }, {timeout: 60000});
  expect(productsFilteredResponse.status()).toBe(200);

  const filteredCards = page.getByTestId('product-card');
  const filteredCount = await filteredCards.count();
  expect(filteredCount).toBeLessThan(nonFilteredCount);
});
