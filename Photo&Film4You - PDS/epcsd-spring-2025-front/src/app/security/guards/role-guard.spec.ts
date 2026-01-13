import {TestBed} from '@angular/core/testing';

import {roleGuard} from './role-guard';
import {AuthService} from '@app/core/services/auth.service';
import {UserRole} from '@app/core/enums/user-role.enum';
import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

describe('roleGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => roleGuard(...guardParameters));

  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['hasRole']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      providers: [
        {provide: AuthService, useValue: authServiceSpy},
        {provide: Router, useValue: routerSpy},
        {provide: MatSnackBar, useValue: snackBarSpy}
      ]
    });
  });

  it('should allow access (return true) if the user has the required role', () => {
    authServiceSpy.hasRole.and.returnValue(true);

    const route = {
      data: {roles: [UserRole.ADMIN]}
    } as any as ActivatedRouteSnapshot;

    const state = {} as RouterStateSnapshot;

    const result = executeGuard(route, state);

    expect(result).toBeTrue(); 
    expect(authServiceSpy.hasRole).toHaveBeenCalledWith([UserRole.ADMIN]); 
    expect(routerSpy.navigate).not.toHaveBeenCalled(); 
    expect(snackBarSpy.open).not.toHaveBeenCalled(); 
  });

  it('should deny access, show snackbar and redirect to home if user does NOT have the role', async () => {
    authServiceSpy.hasRole.and.returnValue(false);

    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    const route = {
      data: {roles: [UserRole.ADMIN]}
    } as any as ActivatedRouteSnapshot;

    const state = {} as RouterStateSnapshot;

    const result = executeGuard(route, state);

    expect(await result).toBeFalse();

    expect(authServiceSpy.hasRole).toHaveBeenCalledWith([UserRole.ADMIN]);
    expect(snackBarSpy.open).toHaveBeenCalled(); 
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']); 
  });

  it('should call hasRole with empty array if route has no data', () => {
    authServiceSpy.hasRole.and.returnValue(true);

    const route = {data: {}} as any as ActivatedRouteSnapshot;
    const state = {} as RouterStateSnapshot;

    executeGuard(route, state);

    expect(authServiceSpy.hasRole).toHaveBeenCalledWith([]);
  });
});
