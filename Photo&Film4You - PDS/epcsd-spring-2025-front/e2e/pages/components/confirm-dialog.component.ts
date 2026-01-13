import {BasePage} from '@e2e/pages/base.page';
import {expect} from '@e2e/fixtures/auth';

export class ConfirmDialogComponent extends BasePage {
  public readonly confirmDialogTitle = this.locator('confirm-dialog-title');
  public readonly confirmDialogActions = this.locator('confirm-dialog-actions');
  public readonly confirmButton = this.confirmDialogActions.getByTestId('confirm-button');
  public readonly cancelButton = this.confirmDialogActions.getByTestId('cancel-button');

  async expectDialogTitle(title: string) {
    await expect(this.confirmDialogTitle).toBeVisible();
    await expect(this.confirmDialogTitle).toHaveText(title);
  }

  async confirm() {
    await expect(this.confirmDialogActions).toBeVisible();
    await expect(this.confirmButton).toBeVisible();
    await expect(this.confirmButton).toBeEnabled();
    await this.confirmButton.click();
  }

  async cancel() {
    await expect(this.confirmDialogActions).toBeVisible();
    await expect(this.cancelButton).toBeVisible();
    await expect(this.cancelButton).toBeEnabled();
    await this.cancelButton.click();
  }
}
