import {Component, computed} from '@angular/core';
import {MatButton, MatMiniFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatToolbar, MatToolbarModule} from '@angular/material/toolbar';
import {AsyncPipe, NgOptimizedImage} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '@app/core/services/auth.service';
import {MatDialog} from '@angular/material/dialog';
import {Login} from '@app/features/login/login';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatBadge} from '@angular/material/badge';
import {CartService} from '@app/core/services/cart.service';
import {Observable} from 'rxjs';
import {BookingCart} from '@app/core/models/cart';
import {NotificationDropdown} from '@app/features/notification-dropdown/notification-dropdown';
import {UserRole} from '@app/core/enums/user-role.enum';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatMiniFabButton,
    MatToolbar,
    MatToolbarModule,
    NgOptimizedImage,
    RouterLink,
    MatMenuTrigger,
    MatBadge,
    AsyncPipe,
    NotificationDropdown
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  currentUser = computed(() => this.auth.currentUser());

  isAdmin = computed(() =>
    this.currentUser()?.role === UserRole.ADMIN
  );

  cart$: Observable<BookingCart>;

  constructor(
    private readonly auth: AuthService,
    private readonly dialog: MatDialog,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar,
    private readonly cartService: CartService
  ) {
    this.cart$ = cartService.cart$;
  }

  openLoginDialog(): void {
    this.dialog.open(Login, {
      width: '90vw',
      maxWidth: '400px',
      maxHeight: '90vh'
    });
  }

  logout(): void {
    this.cartService.clear();
    this.auth.logout();
    this.router.navigateByUrl('/').then(() => {
      this.snackBar.open('Sesión cerrada correctamente', 'cerrar', {
        duration: 3000
      });
    });
  }

  avatarUrl(name?: string): string {
    if (!name) {
      return 'https://ui-avatars.com/api/?background=005cbb&color=fff&rounded=true';
    }
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=005cbb&color=fff&bold=true&rounded=true`;
  }
}
