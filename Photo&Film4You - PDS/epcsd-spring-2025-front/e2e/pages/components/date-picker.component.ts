import {BasePage} from '@e2e/pages/base.page';
import {Locator} from '@playwright/test';

export class DatePickerComponent extends BasePage {
  public readonly datePickerPanel = this.locator('date-picker-panel');
  public readonly startDateInput = this.locator('start-date-input');
  public readonly endDateInput = this.locator('end-date-input');
  public readonly applyDateRangeButton = this.locator('apply-date-range-button');

  pad2(n: number): string {
    return String(n).padStart(2, '0');
  }

  formatDDMMYYYYCompact(d: Date): string {
    return `${this.pad2(d.getDate())}${this.pad2(d.getMonth() + 1)}${d.getFullYear()}`;
  }

  async openPanel() {
    return this.togglePanel(true);
  }

  async togglePanel(openPanel: boolean) {
    const isPanelOpen = await this.startDateInput.isVisible();

    if (isPanelOpen !== openPanel) {
      await this.datePickerPanel.click();
    }
  }

  async closeMatDatepicker() {
    await this.page.keyboard.press('Escape');
    await this.page.waitForTimeout(50);
    await this.page.keyboard.press('Escape');
  }

  async typeCompactDate(input: Locator, ddmmyyyy: string) {
    await input.focus();
    await this.closeMatDatepicker();
    await input.clear();
    await input.fill(ddmmyyyy);

    await input.evaluate((el: HTMLInputElement) => {
      el.dispatchEvent(new Event('input', {bubbles: true}));
      el.dispatchEvent(new Event('change', {bubbles: true}));
    });

    await input.press('Tab');
  }

  async fillDateRange(startDate: Date, endDate: Date) {
    await this.openPanel();
    await this.startDateInput.clear();
    await this.endDateInput.clear();

    await this.typeCompactDate(this.startDateInput, this.formatDDMMYYYYCompact(startDate));
    await this.typeCompactDate(this.endDateInput, this.formatDDMMYYYYCompact(endDate));
  }

  async applyFilter() {
    return await Promise.all([
      this.page.waitForResponse((r) =>
        r.url().endsWith('/api/bookings/availability') &&
        r.request().method() === 'POST'
      ),
      this.applyDateRangeButton.click()
    ]);
  }

  async selectDateRange(startDate: Date, endDate: Date) {
    await this.fillDateRange(startDate, endDate);
    await this.applyFilter();
  }
}
