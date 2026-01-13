import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '@app/core/services/auth.service';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatIcon} from '@angular/material/icon';
import {LoadingService} from '@app/core/services/loading-service';
import {LoadingOverlayComponent} from '@app/features/loading-overlay/loading-overlay.component';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatIcon,
    LoadingOverlayComponent,
    MatDivider
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form: FormGroup;
  hidePassword = signal(true);

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<Login>,
    private readonly authService: AuthService,
    private readonly snackBar: MatSnackBar,
    private readonly loadingService: LoadingService
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.snackBar.open("Por favor, complete el formulario correctamente", "Cerrar", {
        duration: 5000
      });
      return;
    }
    this.loadingService.show();

    const {email, password} = this.form.value;
    this.authService.login(email, password).subscribe({
      next: () => {
        this.loadingService.hide();
        this.dialogRef.close();
      },
      error: () => {
        this.loadingService.hide();
        this.snackBar.open("Credenciales incorrectas", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  switchPasswordVisibility(): void {
    this.hidePassword.update(hide => !hide);
  }

  isLoading(): boolean {
    return this.loadingService.isLoading();
  }
}
