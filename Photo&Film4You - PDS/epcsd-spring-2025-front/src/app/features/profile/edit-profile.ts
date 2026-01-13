import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import {UserService} from '@app/core/services/user.service';
import {UserProfile} from '@app/core/models/user/user-profile.model';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.css']
})
export class EditProfile implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  form!: FormGroup;
  loading = signal(false);
  error = signal<string | null>(null);

  user = signal<UserProfile | null>(null);

  ngOnInit(): void {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      phoneNumber: [
        '',
        [
          Validators.required,
          Validators.pattern(/^\d+$/)
        ]
      ]
    });

    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService.getCurrentUser().subscribe({
      next: (u: UserProfile) => {
        this.user.set(u);
        this.form.patchValue({
          fullName: u.fullName,
          phoneNumber: u.phoneNumber
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error al cargar el perfil');
        this.loading.set(false);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const current = this.user();
    if (!current) {
      this.error.set('No se ha podido cargar el usuario actual');
      return;
    }

    const payload: UserProfile = {
      ...current,
      fullName: this.form.value.fullName,
      phoneNumber: this.form.value.phoneNumber
    };

    this.userService.updateCurrentUser(payload).subscribe({
      next: () => {
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Error al guardar el perfil', err);
        this.error.set(err.error?.message || 'Error al guardar los cambios');
      }
    });
  }
}
