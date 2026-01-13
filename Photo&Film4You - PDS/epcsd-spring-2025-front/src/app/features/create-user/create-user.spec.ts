import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError, delay } from 'rxjs';

import { CreateUser } from './create-user';
import { UserService } from '@app/core/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserRole } from '@app/core/enums/user-role.enum';

describe('CreateUser', () => {
  let fixture: ComponentFixture<CreateUser>;
  let component: CreateUser;

  const userServiceSpy = {
    createUser: jasmine.createSpy('createUser')
  };

  const snackBarSpy = {
    open: jasmine.createSpy('open')
  };

  const routerSpy = {
    navigateByUrl: jasmine.createSpy('navigateByUrl').and.returnValue(Promise.resolve(true))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateUser, NoopAnimationsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateUser);
    component = fixture.componentInstance;
    fixture.detectChanges();

    userServiceSpy.createUser.calls.reset();
    snackBarSpy.open.calls.reset();
    routerSpy.navigateByUrl.calls.reset();
  });

  function fillValidForm() {
    component.form.setValue({
      fullName: 'Pepe',
      email: 'pepe@example.com',
      password: 'Password123',
      phoneNumber: '600600600',
      role: UserRole.USER
    });
  }

  it('should not submit when form invalid (marks touched)', () => {
    component.form.patchValue({ fullName: '' }); // required
    component.submit();

    expect(component.form.touched).toBeTrue();
    expect(userServiceSpy.createUser).not.toHaveBeenCalled();
  });

  it('should submit OK -> snackbar + navigate /', fakeAsync(() => {
    fillValidForm();
    userServiceSpy.createUser.and.returnValue(of(123).pipe(delay(0)));

    component.submit();
    expect(component.loading).toBeTrue();

    tick(0);

    expect(component.loading).toBeFalse();
    expect(snackBarSpy.open).toHaveBeenCalled();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/');
  }));

  it('should handle 409 -> set emailExists error + snackbar', fakeAsync(() => {
    fillValidForm();
    userServiceSpy.createUser.and.returnValue(
      throwError(() => ({ status: 409 }))
    );

    component.submit();
    tick();

    expect(component.loading).toBeFalse();
    expect(component.form.controls['email'].hasError('emailExists')).toBeTrue();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Ya existe un usuario con ese email',
      'Cerrar',
      jasmine.objectContaining({ duration: 5000 })
    );
  }));

  it('should handle generic error -> snackbar with fallback', fakeAsync(() => {
    fillValidForm();
    userServiceSpy.createUser.and.returnValue(
      throwError(() => ({ status: 500, error: { message: 'Boom' } }))
    );

    component.submit();
    tick();

    expect(component.loading).toBeFalse();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Boom',
      'Cerrar',
      jasmine.any(Object)
    );
  }));
});
