// e2e/pages/base.page.ts
import {Locator, Page} from '@playwright/test';

export abstract class BasePage {
  constructor(protected readonly page: Page) {
  }

  locator(testId: string): Locator {
    return this.page.getByTestId(testId);
  }
}
