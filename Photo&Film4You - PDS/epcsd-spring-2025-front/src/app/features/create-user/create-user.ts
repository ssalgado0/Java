import {Component} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

import {UserService, CreateUserRequest} from '@app/core/services/user.service';
import {UserRole} from '@app/core/enums/user-role.enum';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './create-user.html',
  styleUrl: './create-user.css'
})
export class CreateUser {

  roles = [UserRole.USER, UserRole.ADMIN, UserRole.EXTERNAL];
  loading = false;

  form!: ReturnType<FormBuilder['group']>;

  constructor(
    private readonly fb: FormBuilder,
    private readonly userService: UserService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router
  ) {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      phoneNumber: ['', [
        Validators.required,
        Validators.pattern(/^\d{9,15}$/) // habilitado solo a números, 9–15 dígitos
      ]],
      role: [UserRole.USER, Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload = this.form.getRawValue() as CreateUserRequest;

    this.userService.createUser(payload).subscribe({
      next: (id) => {
        this.snackBar.open(`Usuario creado (ID: ${id})`, 'Cerrar', {
          duration: 3000
        });
        this.loading = false;
        this.router.navigateByUrl('/');
      },
      error: (err) => {

        if (err?.status === 409) {
          this.form.controls['email'].setErrors({ emailExists: true });
          this.form.controls['email'].markAsTouched();

          this.snackBar.open(
            'Ya existe un usuario con ese email',
            'Cerrar',
            { duration: 5000, panelClass: ['snackbar-error'] }
          );

          this.loading = false;
          return;
        }

        const msg =
          (typeof err?.error?.message === 'string' && err.error.message) ||
          (typeof err?.error === 'string' && err.error) ||
          `Error (${err?.status ?? ''}) creando usuario`;

        this.snackBar.open(msg, 'Cerrar', {
          duration: 5000,
          panelClass: ['snackbar-error']
        });

        this.loading = false;
      }
    });
  }

}
