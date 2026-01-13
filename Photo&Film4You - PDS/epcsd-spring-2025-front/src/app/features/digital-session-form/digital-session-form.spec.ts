import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DigitalSessionForm} from './digital-session-form';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {ActivatedRoute, Router, UrlTree} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {AuthService} from '@app/core/services/auth.service';
import {of, throwError} from 'rxjs';
import {DigitalSession} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';

describe('DigitalSessionForm', () => {
  let component: DigitalSessionForm;
  let fixture: ComponentFixture<DigitalSessionForm>;
  let digitalServiceSpy: jasmine.SpyObj<DigitalService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockSession: DigitalSession = {
    id: 1,
    description: 'Test Session',
    email: 'test@test.com',
    status: DigitalStatus.AVAILABLE
  };

  beforeEach(async () => {
    digitalServiceSpy = jasmine.createSpyObj('DigitalService', [
      'getDigitalSessionById',
      'createDigitalSession',
      'updateDigitalSession',
      'deleteDigitalSession',
      'countDigitalItemsBySessionId'
    ]);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['currentUser']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    routerSpy = Object.assign(jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl']), {
      events: of(null)
    });
    routerSpy.createUrlTree.and.returnValue({} as UrlTree);
    routerSpy.serializeUrl.and.returnValue('');
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [DigitalSessionForm, MatSnackBarModule, MatDialogModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({get: (key: string) => (key === 'id' ? '1' : null)})
          }
        },
        {provide: Router, useValue: routerSpy},
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: AuthService, useValue: authServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy},
        {provide: MatDialog, useValue: dialogSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DigitalSessionForm);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(of(mockSession));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(0));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should initialize in edit mode and load session', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(of(mockSession));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(5));
    fixture.detectChanges();

    expect(component.isEditMode).toBeTrue();
    expect(component.sessionId).toBe(1);
    expect(digitalServiceSpy.getDigitalSessionById).toHaveBeenCalledWith(1);
    expect(digitalServiceSpy.countDigitalItemsBySessionId).toHaveBeenCalledWith(1);
    expect(component.sessionForm.value).toEqual({
      description: mockSession.description,
      email: mockSession.email
    });
    expect(component.digitalItems).toBe(5);
  });

  it('should handle error when loading session', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(throwError(() => new Error('Error')));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(0));
    fixture.detectChanges();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Ha habido un error cargando los detalles de la sesión",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should create a new session', () => {
    // Override ActivatedRoute for create mode
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [DigitalSessionForm, MatSnackBarModule, MatDialogModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({get: () => null})
          }
        },
        {provide: Router, useValue: routerSpy},
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: AuthService, useValue: authServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy},
        {provide: MatDialog, useValue: dialogSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DigitalSessionForm);
    component = fixture.componentInstance;

    authServiceSpy.currentUser.and.returnValue({email: 'user@test.com'} as any);
    fixture.detectChanges();

    expect(component.isEditMode).toBeFalse();
    expect(component.sessionForm.value.email).toBe('user@test.com');

    component.sessionForm.patchValue({description: 'New Session'});
    digitalServiceSpy.createDigitalSession.and.returnValue(of(123));

    component.submit();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.createDigitalSession).toHaveBeenCalledWith('New Session', 'user@test.com');
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Sesión creada correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 123]);
  });

  it('should update an existing session', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(of(mockSession));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(0));
    fixture.detectChanges();

    component.sessionForm.patchValue({description: 'Updated Session'});
    digitalServiceSpy.updateDigitalSession.and.returnValue(of(true));

    component.submit();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.updateDigitalSession).toHaveBeenCalledWith(1, 'Updated Session', 'test@test.com');
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Sesión editada correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions', 1]);
  });

  it('should delete session if no items', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(of(mockSession));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(0));
    fixture.detectChanges();

    const dialogRefSpy = jasmine.createSpyObj({afterClosed: of(true)});
    dialogSpy.open.and.returnValue(dialogRefSpy);
    digitalServiceSpy.deleteDigitalSession.and.returnValue(of(true));

    component.delete();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(digitalServiceSpy.deleteDigitalSession).toHaveBeenCalledWith(1);
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Sesión eliminada correctamente",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should not delete session if it has items', () => {
    digitalServiceSpy.getDigitalSessionById.and.returnValue(of(mockSession));
    digitalServiceSpy.countDigitalItemsBySessionId.and.returnValue(of(5));
    fixture.detectChanges();

    component.delete();

    expect(dialogSpy.open).not.toHaveBeenCalled();
  });
});
