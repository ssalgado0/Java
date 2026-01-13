import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '@app/core/services/auth.service';
import {UserRole} from '@app/core/enums/user-role.enum';
import {MatSnackBar} from '@angular/material/snack-bar';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  const expectedRoles: UserRole[] = route.data['roles'] || [];

  if (authService.hasRole(expectedRoles)) {
    return true;
  }

  snackBar.open('No tienes permisos para acceder a esta sección', 'Cerrar', {
    duration: 5000,
    panelClass: ['snackbar-error']
  });

  return router.navigate(['/']).then(() => false);
};
