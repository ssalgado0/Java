import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DigitalItemForm} from './digital-item-form';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {ActivatedRoute, Router, UrlTree} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {of, throwError} from 'rxjs';
import {DigitalItem} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';

describe('DigitalItemForm', () => {
  let component: DigitalItemForm;
  let fixture: ComponentFixture<DigitalItemForm>;
  let digitalServiceSpy: jasmine.SpyObj<DigitalService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockItem: DigitalItem = {
    id: 1,
    digitalSessionId: 1,
    description: 'Test Item',
    lat: 10,
    lon: 20,
    link: 'http://test.com',
    status: DigitalStatus.AVAILABLE
  };

  beforeEach(async () => {
    digitalServiceSpy = jasmine.createSpyObj('DigitalService', [
      'getDigitalItemById',
      'addDigitalItem',
      'updateDigitalItem',
      'deleteDigitalItem'
    ]);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    routerSpy = Object.assign(jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl']), {
      events: of(null)
    });
    routerSpy.createUrlTree.and.returnValue({} as UrlTree);
    routerSpy.serializeUrl.and.returnValue('');
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [DigitalItemForm, MatSnackBarModule, MatDialogModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => (key === 'sessionId' ? '1' : null)
              }
            },
            paramMap: of({get: (key: string) => (key === 'itemId' ? '1' : null)})
          }
        },
        {provide: Router, useValue: routerSpy},
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy},
        {provide: MatDialog, useValue: dialogSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DigitalItemForm);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    digitalServiceSpy.getDigitalItemById.and.returnValue(of(mockItem));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should initialize in edit mode and load item', () => {
    digitalServiceSpy.getDigitalItemById.and.returnValue(of(mockItem));
    fixture.detectChanges();

    expect(component.isEditMode).toBeTrue();
    expect(component.itemId).toBe(1);
    expect(digitalServiceSpy.getDigitalItemById).toHaveBeenCalledWith(1);
    expect(component.itemForm.value).toEqual({
      description: mockItem.description,
      lat: mockItem.lat,
      lon: mockItem.lon,
      link: mockItem.link
    });
  });

  it('should handle error when loading item', () => {
    digitalServiceSpy.getDigitalItemById.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Ha habido un error cargando los detalles del elemento",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 1]);
  });

  it('should create a new item', () => {
    // Override ActivatedRoute for create mode
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [DigitalItemForm, MatSnackBarModule, MatDialogModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => (key === 'sessionId' ? '1' : null)
              }
            },
            paramMap: of({get: () => null})
          }
        },
        {provide: Router, useValue: routerSpy},
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy},
        {provide: MatDialog, useValue: dialogSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DigitalItemForm);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.isEditMode).toBeFalse();

    component.itemForm.patchValue({
      description: 'New Item',
      lat: 10,
      lon: 20,
      link: 'http://test.com'
    });
    digitalServiceSpy.addDigitalItem.and.returnValue(of(123));

    component.submit();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.addDigitalItem).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Elemento creado correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 1]);
  });

  it('should update an existing item', () => {
    digitalServiceSpy.getDigitalItemById.and.returnValue(of(mockItem));
    fixture.detectChanges();

    component.itemForm.patchValue({description: 'Updated Item'});
    digitalServiceSpy.updateDigitalItem.and.returnValue(of(true));

    component.submit();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.updateDigitalItem).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Elemento editado correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 1]);
  });

  it('should delete item', () => {
    digitalServiceSpy.getDigitalItemById.and.returnValue(of(mockItem));
    fixture.detectChanges();

    const dialogRefSpy = jasmine.createSpyObj({afterClosed: of(true)});
    dialogSpy.open.and.returnValue(dialogRefSpy);
    digitalServiceSpy.deleteDigitalItem.and.returnValue(of(true));

    component.delete();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(digitalServiceSpy.deleteDigitalItem).toHaveBeenCalledWith(1);
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Elemento eliminado correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 1]);
  });
});
