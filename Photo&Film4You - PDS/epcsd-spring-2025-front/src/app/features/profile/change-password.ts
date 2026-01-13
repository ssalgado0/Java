import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UserService } from '@app/core/services/user.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './change-password.html',
  styleUrls: ['./change-password.css']
})
export class ChangePassword {

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  form: FormGroup;
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  constructor() {
    this.form = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmNewPassword: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmNewPassword } = this.form.value;

    if (newPassword !== confirmNewPassword) {
      this.error.set('Las contraseñas nuevas no coinciden');
      this.success.set(null);
      return;
    }

    this.userService.changePassword({ currentPassword, newPassword }).subscribe({
      next: () => {
        this.error.set(null);

        this.snackBar.open(
          'Contraseña actualizada correctamente',
          'Cerrar',
          { duration: 3000 }
        );

        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Error al cambiar la contraseña', err);
        this.error.set(err.error?.message || 'Error al cambiar la contraseña');
        this.success.set(null);
      }
    });
  }
}
