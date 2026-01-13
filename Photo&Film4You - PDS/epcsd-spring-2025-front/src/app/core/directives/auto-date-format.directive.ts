import {Directive, HostListener} from '@angular/core';

@Directive({
  selector: '[autoDateFormatDirective]',
  standalone: true
})
export class AutoDateFormatDirective {

  constructor() {
  }

  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const inputEvent = event as InputEvent;
    const input = inputEvent.target as HTMLInputElement;

    // If the user is deleting, do not enforce the format to avoid blocking them
    if (inputEvent.inputType === 'deleteContentBackward' || inputEvent.inputType === 'deleteContentForward') {
      return;
    }

    // 1. Remove all non-digit characters
    let trimmed = input.value.replace(/\D/g, '');

    // 2. Limit to 8 digits (DDMMYYYY)
    if (trimmed.length > 8) {
      trimmed = trimmed.substring(0, 8);
    }

    // 3. Apply the DD/MM/YYYY format
    let formatted = '';
    if (trimmed.length > 4) {
      // If more than 4 numbers: DD/MM/Rest
      formatted = `${trimmed.slice(0, 2)}/${trimmed.slice(2, 4)}/${trimmed.slice(4)}`;
    } else if (trimmed.length > 2) {
      // If more than 2 numbers: DD/Rest
      formatted = `${trimmed.slice(0, 2)}/${trimmed.slice(2)}`;
    } else {
      // If 2 or fewer numbers: DD
      formatted = trimmed;
    }

    // 4. Update the input value
    input.value = formatted;
  }
}
