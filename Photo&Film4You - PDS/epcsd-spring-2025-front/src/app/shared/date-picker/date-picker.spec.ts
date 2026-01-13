import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DatePicker} from './date-picker';
import {provideNativeDateAdapter} from '@angular/material/core';

describe('DatePicker', () => {
  let component: DatePicker;
  let fixture: ComponentFixture<DatePicker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DatePicker],
      providers: [
        provideNativeDateAdapter()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DatePicker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate invalid range when end is same or before start', () => {
    const start = new Date(2025, 0, 10); 
    const endSame = new Date(2025, 0, 10);   
    const endBefore = new Date(2025, 0, 9); 

    component.range.controls.start.setValue(start);
    component.range.controls.end.setValue(endSame);
    expect(component.range.hasError('invalidRange')).toBeTrue();

    component.range.controls.end.setValue(endBefore);
    expect(component.range.hasError('invalidRange')).toBeTrue();
  });

  it('should consider valid range when end is after start', () => {
    const start = new Date();
    const end = new Date();
    end.setDate(start.getDate() + 1);

    component.range.controls.start.setValue(start);
    component.range.controls.end.setValue(end);

    expect(component.range.valid).toBeTrue();
    expect(component.range.hasError('invalidRange')).toBeFalse();
  });

  it('should consider invalid range when start is before today', () => {
    const start = new Date(2025, 0, 1);
    const end = new Date(2025, 0, 5);

    component.range.controls.start.setValue(start);
    component.range.controls.end.setValue(end);

    expect(component.range.valid).toBeFalse();
    expect(component.range.hasError('invalidRange')).toBeFalse();
  });

  it('normalizeDate should return UTC-normalized date (time set to 00:00 UTC)', () => {
    const date = new Date(2025, 4, 15, 13, 30, 45); 
    const normalized = component.normalizeDate(date);


    expect(normalized.getUTCFullYear()).toBe(2025);
    expect(normalized.getUTCMonth()).toBe(4);
    expect(normalized.getUTCDate()).toBe(15);

    expect(normalized.getUTCHours()).toBe(0);
    expect(normalized.getUTCMinutes()).toBe(0);
    expect(normalized.getUTCSeconds()).toBe(0);
  });

  it('onSubmit should emit rangeSelected with normalized dates and close the panel when form is valid', () => {
    const start = new Date();
    const end = new Date();
    end.setDate(start.getDate() + 1);

    component.range.controls.start.setValue(start);
    component.range.controls.end.setValue(end);

    spyOn(component.rangeSelected, 'emit');
    component.datePicketPanel = {close: jasmine.createSpy('close')} as any;

    component.onSubmit();

    expect(component.rangeSelected.emit).toHaveBeenCalled();
    const emittedArg = (component.rangeSelected.emit as jasmine.Spy).calls.mostRecent().args[0];
    expect(emittedArg.start instanceof Date).toBeTrue();
    expect(emittedArg.end instanceof Date).toBeTrue();

    expect(emittedArg.start.getUTCHours()).toBe(0);
    expect(emittedArg.end.getUTCHours()).toBe(0);

    expect(component.datePicketPanel.close).toHaveBeenCalled();
  });

  it('onSubmit should not emit when form is invalid', () => {
    const start = new Date(2025, 0, 10);
    const end = new Date(2025, 0, 9); 

    component.range.controls.start.setValue(start);
    component.range.controls.end.setValue(end);

    spyOn(component.rangeSelected, 'emit');
    component.onSubmit();

    expect(component.rangeSelected.emit).not.toHaveBeenCalled();
  });

  it('onReset should emit rangeReseted', () => {
    spyOn(component.rangeReseted, 'emit');
    component.onReset();
    expect(component.rangeReseted.emit).toHaveBeenCalled();
  });
});
