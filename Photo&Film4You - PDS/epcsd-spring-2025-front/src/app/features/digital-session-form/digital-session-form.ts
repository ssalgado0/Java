import {Component, computed, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {LoadingService} from '@app/core/services/loading-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {filter, map, of, switchMap, tap} from 'rxjs';
import {DigitalService} from '@app/core/services/digital.service';
import {AuthService} from '@app/core/services/auth.service';
import {ConfirmDialog} from '@app/shared/confirm-dialog/confirm-dialog';
import {MatDialog} from '@angular/material/dialog';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-digital-session-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
    MatIcon,
    MatError,
    RouterLink,
    MatTooltip
  ],
  templateUrl: './digital-session-form.html',
})
export class DigitalSessionForm implements OnInit {

  sessionForm: FormGroup;
  isEditMode: boolean = false;
  sessionId: number | null = null;
  digitalItems: number | null = null;
  currentUser = computed(() => this.auth.currentUser());

  constructor(
    private readonly fb: FormBuilder,
    private readonly loadingService: LoadingService,
    private readonly digitalService: DigitalService,
    private readonly auth: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar,
    private readonly dialog: MatDialog,
  ) {
    this.sessionForm = this.fb.group({
      description: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.loadingService.show();

    this.route.paramMap
      .pipe(
        tap(() => {
          this.loadingService.show();
          this.sessionForm.disable();
        }),
        switchMap(params => {
          const id = params.get('id');
          this.sessionId = id ? Number.parseInt(id) : null;
          this.isEditMode = !!this.sessionId;

          if (!this.isEditMode) {
            return of(null);
          }

          this.refreshDigitalItems(this.sessionId!);
          return this.digitalService.getDigitalSessionById(this.sessionId!);
        })
      )
      .subscribe({
        next: session => {
          if (session) {
            this.sessionForm.patchValue({
              description: session.description,
              email: session.email
            })
          } else {
            this.sessionForm.reset();
            this.sessionForm.patchValue({
              email: this.currentUser()?.email || ''
            });
          }
          this.loadingService.hide();
          this.sessionForm.enable();
        },
        error: () => {
          this.loadingService.hide();
          this.snackBar.open("Ha habido un error cargando los detalles de la sesión", "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
          this.router.navigate(['/sessions']);
        }
      });
  }

  refreshDigitalItems(sessionId: number): void {
    this.digitalService.countDigitalItemsBySessionId(sessionId)
      .subscribe({
        next: count => this.digitalItems = count,
        error: () => {
          this.snackBar.open("Ha habido un error recuperando el número de elementos de la sesión", "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        }
      })
  }

  delete(): void {
    if (this.digitalItems !== 0) {
      return;
    }

    this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Eliminar sesión digital',
        message: '¿Seguro que quieres eliminar esta sesión digital? Esta acción no se puede deshacer.',
        confirmText: 'Eliminar',
        cancelText: 'Cancelar'
      }
    })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.digitalService.deleteDigitalSession(this.sessionId!))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Sesión eliminada correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['snackbar-success']
          });

          this.router.navigate(['/sessions']);
        },
        error: () => {
          this.snackBar.open('Ha habido un error eliminando la sesión', "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        }
      });
  }

  submit(): void {
    if (this.sessionForm.invalid) {
      this.sessionForm.markAllAsTouched();
      return;
    }

    this.loadingService.show();

    const {description, email} = this.sessionForm.value;

    const request = this.isEditMode && this.sessionId
      ? this.digitalService.updateDigitalSession(this.sessionId, description, email)
        .pipe(map(ok => {
          if (!ok) throw new Error('Update returned false');
          return this.sessionId!;
        }))
      : this.digitalService.createDigitalSession(description, email);

    request.subscribe({
      next: id => {
        this.loadingService.hide();
        this.snackBar.open(`Sesión ${this.isEditMode ? 'editada' : 'creada'} correctamente`, 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });

        this.router.navigate(['/sessions', id]);
      },
      error: () => {
        this.loadingService.hide();
        this.snackBar.open(`Ha habido un error ${this.isEditMode ? 'editando' : 'creando'} los detalles de la sesión`, "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }
}
