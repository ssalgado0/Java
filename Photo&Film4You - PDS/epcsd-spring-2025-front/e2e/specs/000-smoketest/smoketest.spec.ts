import {expect, test} from '@e2e/coverage.config';

test('has title and navigation', async ({page}) => {

  // Go to the starting url for a page.
  await page.goto('/');
  await expect(page).toHaveTitle('Photo&Film4You');

  await page.goto('/products')
  await expect(page.getByRole('heading', {name: 'Productos'})).toBeVisible();
});
