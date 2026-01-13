import {Component, DebugElement} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AutoDateFormatDirective} from './auto-date-format.directive';

@Component({
  standalone: true,
  imports: [AutoDateFormatDirective],
  template: `<input type="text" autoDateFormatDirective>`
})
class TestHostComponent {
}

describe('AutoDateFormatDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let inputEl: HTMLInputElement;
  let debugEl: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AutoDateFormatDirective,
        TestHostComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    debugEl = fixture.debugElement.query(By.css('input'));
    inputEl = debugEl.nativeElement;
    fixture.detectChanges();
  });

  function triggerInput(value: string, inputType: string = 'insertText') {
    inputEl.value = value;

    const event = new InputEvent('input', {
      bubbles: true,
      cancelable: true,
      inputType: inputType
    });

    inputEl.dispatchEvent(event);
    fixture.detectChanges();
  }

  it('should create', () => {
    const directiveInstance = debugEl.injector.get(AutoDateFormatDirective);
    expect(directiveInstance).toBeTruthy();
  });

  it('should add a slash after writing the day (2 digits)', () => {
    triggerInput('12');
    triggerInput('123');
    expect(inputEl.value).toBe('12/3');
  });

  it('should add the second slash after the month (4 digits)', () => {
    triggerInput('12052');
    expect(inputEl.value).toBe('12/05/2');
  });

  it('should remove non-numeric characters', () => {
    triggerInput('12a3b');
    expect(inputEl.value).toBe('12/3');
  });

  it('should limit the length to 8 digits (full format DD/MM/YYYY)', () => {
    triggerInput('12052025999');
    expect(inputEl.value).toBe('12/05/2025');
  });

  it('should NOT format if the user is deleting content. ', () => {
    inputEl.value = '12/';

    const event = new InputEvent('input', {
      bubbles: true,
      inputType: 'deleteContentBackward'
    });
    inputEl.dispatchEvent(event);
    fixture.detectChanges();

    expect(inputEl.value).toBe('12/');
  });
});
