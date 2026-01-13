import {Component, OnInit, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {UserService} from '@app/core/services/user.service';
import {UserProfile} from '@app/core/models/user/user-profile.model';
import {AuthService} from '@app/core/services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-profile',
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
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})

export class Profile implements OnInit {

  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  user = signal<UserProfile | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadProfile();
  }

loadProfile(): void {
  this.loading.set(true);
  this.error.set(null);

  this.userService.getCurrentUser().subscribe({
    next: (u) => {
      this.user.set(u);
      this.loading.set(false);
    },
    error: (err) => {
      console.error('Error cargando perfil', err);
      this.error.set('Error al cargar el perfil');
      this.loading.set(false);
    }
  });
}



  get currentUserName(): string {
    return this.authService.currentUser()?.fullName ?? '';
  }
}
