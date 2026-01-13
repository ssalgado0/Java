import {TestBed} from '@angular/core/testing';
import {ChangePassword} from './change-password';
import {UserService} from '@app/core/services/user.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

describe('ChangePassword component (validaciones)', () => {
  let component: ChangePassword;

  const userServiceMock = {
    changePassword: jasmine.createSpy('changePassword'),
  };

  const routerMock = {
    navigate: jasmine.createSpy('navigate'),
  };

  const snackBarMock = {
    open: jasmine.createSpy('open'),
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: () => null,
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePassword],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        {provide: ActivatedRoute, useValue: activatedRouteMock},
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(ChangePassword);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('formInvalido_siFaltanCampos', () => {
    component.form.setValue({
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    });

    expect(component.form.invalid).toBeTrue();
  });

  it('formInvalido_siNewPasswordMenosDe8', () => {
    component.form.setValue({
      currentPassword: 'oldpass123',
      newPassword: '1234567',
      confirmNewPassword: '1234567',
    });

    const control = component.form.get('newPassword');
    expect(control?.hasError('minlength')).toBeTrue();
  });
});
