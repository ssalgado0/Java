import {TestBed} from '@angular/core/testing';
import {CustomDateAdapter} from './custom-date-adapter';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {Platform} from '@angular/cdk/platform';

describe('CustomDateAdapter', () => {
  let adapter: CustomDateAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CustomDateAdapter,
        {provide: MAT_DATE_LOCALE, useValue: 'es-ES'},
        Platform
      ]
    });
    adapter = TestBed.inject(CustomDateAdapter);
  });

  it('should be created', () => {
    expect(adapter).toBeTruthy();
  });

  describe('parse', () => {
    it('should parse "DD/MM/YYYY" string correctly', () => {
      const date = adapter.parse('01/06/2026');
      expect(date).toEqual(new Date(2026, 5, 1)); // Month is 0-indexed
    });

    it('should parse "D/M/YYYY" string correctly', () => {
      const date = adapter.parse('1/6/2026');
      expect(date).toEqual(new Date(2026, 5, 1));
    });

    it('should return null for invalid date string', () => {
      const date = adapter.parse('invalid-date');
      expect(date).toBeNull();
    });

    it('should handle timestamps', () => {
      const timestamp = new Date(2026, 5, 1).getTime();
      const date = adapter.parse(timestamp);
      expect(date).toEqual(new Date(2026, 5, 1));
    });
  });

  describe('format', () => {
    it('should format date as "DD/MM/YYYY" with leading zeros', () => {
      const date = new Date(2026, 5, 1); // June 1st, 2026
      const formatted = adapter.format(date, 'DD/MM/YYYY');
      expect(formatted).toBe('01/06/2026');
    });

    it('should format date as "DD/MM/YYYY" correctly for double digits', () => {
      const date = new Date(2026, 10, 15); // November 15th, 2026
      const formatted = adapter.format(date, 'DD/MM/YYYY');
      expect(formatted).toBe('15/11/2026');
    });

    it('should fallback to super format for other formats', () => {
      const date = new Date(2026, 5, 1);
      // Using a standard format that NativeDateAdapter handles, e.g., standard locale formatting
      // We can't easily predict the output of super.format without knowing the exact locale implementation details for arbitrary strings,
      // but we can check it doesn't crash or return the DD/MM/YYYY format.
      // However, NativeDateAdapter usually expects an object or specific strings for displayFormat if using MatDateFormats.
      // Let's just test that it calls super (implicitly) or returns something reasonable.
      // Actually, NativeDateAdapter uses Intl.DateTimeFormat.

      // Let's try a standard format object if possible, or just a different string.
      // If we pass something that is not 'DD/MM/YYYY', it should go to super.
      // Let's assume 'YYYY-MM-DD' is NOT handled by our override.

      // Note: NativeDateAdapter implementation of format:
      // format(date: Date, displayFormat: Object): string {
      //   if (!this.isValid(date)) { throw Error('Junk'); }
      //   if (this._useUtcForDisplay) { ... }
      //   const dtf = new Intl.DateTimeFormat(this.locale, displayFormat);
      //   return this._stripDirectionalityCharacters(this._format(dtf, date));
      // }

      // So displayFormat is passed to Intl.DateTimeFormat.
      const displayFormat = {year: 'numeric', month: 'numeric', day: 'numeric'};
      const formatted = adapter.format(date, displayFormat);

      // With es-ES, this should be roughly "1/6/2026" or "1/06/2026" depending on browser/node implementation of Intl.
      // The important thing is that it works.
      expect(formatted).toBeTruthy();
      expect(formatted).toContain('2026');
    });
  });
});
