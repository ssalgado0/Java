import {ComponentFixture, TestBed} from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '@app/core/services/auth.service';
import { of, throwError } from 'rxjs';
import {Login} from './login';
import { LoadingService } from '@app/core/services/loading-service';
import {LoginResponse} from '@app/core/models/auth';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let dialogRefMock: jasmine.SpyObj<MatDialogRef<Login>>;
  let loadingServiceMock: jasmine.SpyObj<LoadingService>;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['login']);
    snackBarMock = jasmine.createSpyObj('MatSnackBar', ['open']);
    dialogRefMock = jasmine.createSpyObj('MatDialogRef', ['close']);
    loadingServiceMock = jasmine.createSpyObj('LoadingService', ['show', 'hide', 'isLoading']);

    await TestBed.configureTestingModule({
      imports: [Login, HttpClientTestingModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: authServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: LoadingService, useValue: loadingServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('debería cerrar el diálogo y ocultar loading en login exitoso', () => {
    component.form.setValue({ email: 'test@test.com', password: '1234' });
    authServiceMock.login.and.returnValue(of({ token: 'fake-token' } as LoginResponse));

    component.submit();

    expect(loadingServiceMock.show).toHaveBeenCalled();
    expect(loadingServiceMock.hide).toHaveBeenCalled();
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('debería cerrar el diálogo al cancelar', () => {
    component.cancel();
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('debería alternar la visibilidad de la contraseña', () => {
    const initial = component.hidePassword();
    component.switchPasswordVisibility();
    expect(component.hidePassword()).toBe(!initial);
  });

  it('debería devolver el estado de carga', () => {
    loadingServiceMock.isLoading.and.returnValue(true);
    loadingServiceMock.show();
    expect(component.isLoading()).toBeTrue();
  });
});
