import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';

import { TableFilter } from './table-filter';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

describe('TableFilter (TDD & RxJS Validation)', () => {
  let component: TableFilter;
  let fixture: ComponentFixture<TableFilter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TableFilter,
        ReactiveFormsModule,
        MatInputModule,
        MatFormFieldModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableFilter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe emitir el término de filtrado solo después del debounce time (300ms)', fakeAsync(() => {
    const filterSpy = spyOn(component.filterTerm, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input') as HTMLInputElement;

    inputElement.value = 't';
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    tick(299);
    expect(filterSpy).not.toHaveBeenCalled();

    tick(1);
    expect(filterSpy).toHaveBeenCalledTimes(1);

    inputElement.value = 'test1';
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    tick(300);
    expect(filterSpy).toHaveBeenCalledWith('test1' as any);

    flush();
  }));

  it('debe emitir el término solo si cambia (distinctUntilChanged)', fakeAsync(() => {
    const filterSpy = spyOn(component.filterTerm, 'emit');
    const inputElement = fixture.nativeElement.querySelector('input') as HTMLInputElement;

    inputElement.value = 'a';
    inputElement.dispatchEvent(new Event('input'));
    tick(300);
    expect(filterSpy).toHaveBeenCalledTimes(1);

    inputElement.value = 'a';
    inputElement.dispatchEvent(new Event('input'));
    tick(300);
    expect(filterSpy).toHaveBeenCalledTimes(1);

    flush();
  }));


});
