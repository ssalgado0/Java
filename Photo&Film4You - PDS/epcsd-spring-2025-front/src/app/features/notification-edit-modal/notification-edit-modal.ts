import {Component, Inject, inject, OnInit, signal} from '@angular/core';
import {DigitalService} from '@app/core/services/digital.service';
import {NotificationService} from '@app/core/services/notification.service';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {DigitalItem} from '@app/core/models/digital';
import { Notification } from '@app/core/models/notification';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-notification-edit-modal',
  standalone: true,
  imports: [CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule],
  templateUrl: './notification-edit-modal.html',
  styleUrl: './notification-edit-modal.css',
})
export class NotificationEditModal implements OnInit {

  private readonly digitalService = inject(DigitalService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogRef = inject(MatDialogRef<NotificationEditModal>);

  item = signal<DigitalItem | null>(null);
  loading = signal(true);

  constructor(@Inject(MAT_DIALOG_DATA) public data: { notification: Notification }) {}

  ngOnInit(): void {
    this.loadDigitalItemDetails();
  }

  private extractIdFromMessage(mensaje: string): number {
    const regexId: RegExp = /\bID\b\s+"(\d+)"/;
    const match = regexId.exec(mensaje);


    if (match == null) {
      throw new Error('Formato de mensaje inválido: No se encontró un ID entre comillas.');
    }

    const idNumeric = parseInt(match[1], 10);

    if (isNaN(idNumeric)) {
      throw new Error('El valor extraído no es un número válido.');
    }

    return idNumeric;
  }

  private loadDigitalItemDetails(): void {

    let digitalItemId = this.extractIdFromMessage(this.data.notification.message);

    this.digitalService.getDigitalItemById(digitalItemId).subscribe({
      next: (res) => {
        this.item.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error recuperando DigitalItem:', err);
        this.loading.set(false);
      }
    });
  }

  /**
   * Ejecuta la acción sobre el DigitalItem y marca la notificación como leída
   */
  updateStatus(available: boolean): void {
    const itemId = this.item()?.id;
    if (!itemId) return;

    // Orquestación de servicios: Primero el estado del item, luego la notificación
    const action$ = available
      ? this.digitalService.approveDigitalItem(itemId) // Llama a PATCH /approveDigitalItem/{id}
      : this.digitalService.rejectDigitalItem(itemId);  // Llama a PATCH /rejectDigitalItem/{id}

    action$.subscribe({
      next: () => {
        // Marcamos como leída tras el éxito de la operación principal
        this.notificationService.markAsRead(this.data.notification.id).subscribe({
          next: () => this.dialogRef.close(true),
          error: (err) => console.error('Error al marcar notificación:', err)
        });
      }
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
