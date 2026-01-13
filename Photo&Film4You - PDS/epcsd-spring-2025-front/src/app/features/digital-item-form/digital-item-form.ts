import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {LoadingService} from '@app/core/services/loading-service';
import {DigitalService} from '@app/core/services/digital.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {filter, map, of, switchMap, tap} from 'rxjs';
import {DigitalItem} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialog} from '@app/shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-digital-item-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatError,
    MatButton,
    MatIcon,
    RouterLink
  ],
  templateUrl: './digital-item-form.html',
})
export class DigitalItemForm implements OnInit {

  itemForm: FormGroup;
  isEditMode: boolean = false;
  sessionId: number | null = null;
  itemId: number | null = null;

  constructor(
    private readonly fb: FormBuilder,
    private readonly loadingService: LoadingService,
    private readonly digitalService: DigitalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar,
    private readonly dialog: MatDialog,
  ) {
    this.itemForm = this.fb.group({
      description: ['', [Validators.required]],
      lat: ['', [Validators.required]],
      lon: ['', [Validators.required]],
      link: ['', [Validators.required, Validators.pattern(/^(http|https):\/\/[^ "]+$/)]]
    });
  }

  ngOnInit(): void {
    const sId = this.route.snapshot.paramMap.get('sessionId')!;
    this.sessionId = Number.parseInt(sId);

    this.route.paramMap
      .pipe(
        tap(() => {
          this.loadingService.show();
          this.itemForm.disable();
        }),
        switchMap(params => {
          const id = params.get('itemId');
          this.itemId = id ? Number.parseInt(id) : null;
          this.isEditMode = !!this.itemId;

          if (!this.isEditMode) {
            return of(null);
          }

          return this.digitalService.getDigitalItemById(this.itemId as number);
        })
      )
      .subscribe({
        next: session => {
          if (session) {
            this.itemForm.patchValue({
              description: session.description,
              lat: session.lat,
              lon: session.lon,
              link: session.link
            })
          } else {
            this.itemForm.reset();
          }
          this.loadingService.hide();
          this.itemForm.enable();
        },
        error: () => {
          this.loadingService.hide();
          this.snackBar.open("Ha habido un error cargando los detalles del elemento", "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
          this.router.navigate(['/sessions', this.sessionId]);
        }
      });
  }

  delete(): void {
    this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Eliminar elemento digital',
        message: '¿Seguro que quieres eliminar este elemento digital? Esta acción no se puede deshacer.',
        confirmText: 'Eliminar',
        cancelText: 'Cancelar'
      }
    })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.digitalService.deleteDigitalItem(this.itemId!))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Elemento eliminado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['snackbar-success']
          });

          this.router.navigate(['/sessions', this.sessionId!]);
        },
        error: () => {
          this.snackBar.open('Ha habido un error eliminando el elemento', "Cerrar", {
            duration: 5000,
            panelClass: ['snackbar-error']
          });
        }
      });
  }

  submit(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    this.loadingService.show();

    const {description, lat, lon, link} = this.itemForm.value;
    const item: DigitalItem = {
      digitalSessionId: this.sessionId!,
      description,
      lat,
      lon,
      link,
      status: DigitalStatus.AVAILABLE
    };

    const request = this.isEditMode && this.itemId
      ? this.digitalService.updateDigitalItem(this.itemId, item)
        .pipe(map(ok => {
          if (!ok) throw new Error('Update returned false');
          return this.itemId!;
        }))
      : this.digitalService.addDigitalItem(item);

    request.subscribe({
      next: () => {
        this.loadingService.hide();
        this.snackBar.open(`Elemento ${this.isEditMode ? 'editado' : 'creado'} correctamente`, 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });

        this.router.navigate(['/sessions', this.sessionId!]);
      },
      error: () => {
        this.loadingService.hide();
        this.snackBar.open(`Ha habido un error ${this.isEditMode ? 'editando' : 'creando'} los detalles del elemento`, "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }
}
