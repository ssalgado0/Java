import {Component, computed, EventEmitter, input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors
} from '@angular/forms';
import {MatError, MatFormField, MatFormFieldModule, MatHint, MatLabel} from '@angular/material/form-field';
import {
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from '@angular/material/datepicker';
import {MatButton} from '@angular/material/button';
import {
  MatExpansionPanel,
  MatExpansionPanelActionRow,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {DatePipe} from '@angular/common';
import {AutoDateFormatDirective} from '@app/core/directives/auto-date-format.directive';
import {DateRange} from '@app/core/models/date/date-range';

@Component({
  selector: 'app-date-picker',
  imports: [
    MatFormField,
    MatLabel,
    MatDateRangeInput,
    MatDatepickerToggle,
    MatDateRangePicker,
    FormsModule,
    ReactiveFormsModule,
    MatStartDate,
    MatEndDate,
    MatHint,
    MatError,
    MatButton,
    MatExpansionPanel,
    MatExpansionPanelDescription,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIconModule,
    MatIcon,
    MatExpansionPanelActionRow,
    DatePipe,
    MatFormFieldModule,
    AutoDateFormatDirective
  ],
  templateUrl: './date-picker.html',
})
export class DatePicker implements OnChanges {

  @Output() rangeSelected = new EventEmitter<DateRange>();
  @Output() rangeReseted = new EventEmitter<void>();

  selectedRange = input<DateRange | null>(null);
  selectedDateRange = computed(() => this.selectedRange())

  @ViewChild(MatExpansionPanel) datePicketPanel!: MatExpansionPanel;

  minDate: Date;
  range = new FormGroup({
    start: new FormControl<Date | null | undefined>(this.selectedDateRange()?.start),
    end: new FormControl<Date | null | undefined>(this.selectedDateRange()?.end),
  }, {validators: this.rangeValidator});

  constructor() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    this.minDate = today;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedRange']) {
      const range = changes['selectedRange'].currentValue;

      if (range) {
        this.range.get('start')?.setValue(range.start);
        this.range.get('end')?.setValue(range.end);
      } else {
        this.range.reset();
      }
    }
  }

  rangeValidator(control: AbstractControl): ValidationErrors | null {
    const start = control.get('start')?.value;
    const end = control.get('end')?.value;

    if (start && end && end.getTime() <= start.getTime()) {
      return {invalidRange: true};
    }
    return null;
  }

  onSubmit() {
    if (this.range.invalid) {
      return;
    }

    const start = this.range.value.start;
    const end = this.range.value.end;

    if (this.range.valid && start && end) {
      const normalizedStart = this.normalizeDate(start);
      const normalizedEnd = this.normalizeDate(end);

      this.rangeSelected.emit({
        start: normalizedStart,
        end: normalizedEnd
      });
      this.datePicketPanel.close();
    }
  }

  onReset(): void {
    this.rangeReseted.emit()
  }

  normalizeDate(date: Date): Date {
    if (!date) return date;

    return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  }
}
