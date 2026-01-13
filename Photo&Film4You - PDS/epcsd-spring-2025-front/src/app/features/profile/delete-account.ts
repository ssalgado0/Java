import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule, Router} from '@angular/router';
import {UserService} from '@app/core/services/user.service';
import {AuthService} from '@app/core/services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-delete-account',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    CommonModule,
    RouterModule
  ],
  templateUrl: './delete-account.html',
  styleUrls: ['./delete-account.css']
})
export class DeleteAccount {

  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  confirmText = '';
  error = signal<string | null>(null);

  onDelete(): void {
    if (this.confirmText !== 'ELIMINAR') {
      this.error.set('Escribe "ELIMINAR" para confirmar');
      return;
    }

    this.userService.deleteAccount().subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/']);
      },
      error: () => {
        this.error.set('Error al eliminar la cuenta');
      }
    });
  }
}
