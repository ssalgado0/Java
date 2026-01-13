import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NotificationService } from '@app/core/services/notification.service';
import { Notification } from '@app/core/models/notification';
import {NotificationEditModal} from '@app/features/notification-edit-modal/notification-edit-modal';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './notifications.html',
  styleUrls: ['./notifications.css']
})
export class Notifications implements OnInit {
  private readonly notificationService = inject(NotificationService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  notifications: Notification[] = [];
  displayedColumns: string[] = ['type', 'title', 'message', 'createdAt', 'status', 'actions'];
  loading = false;

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    this.notificationService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading notifications:', err);
        this.loading = false;
      }
    });
  }

  markAsRead(notification: Notification): void {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.read = true;
        },
        error: (err) => {
          console.error('Error marking notification as read:', err);
        }
      });
    }
  }

  openEditModal(notification: Notification): void {
    const dialogRef = this.dialog.open(NotificationEditModal, {
      width: '500px',
      data: { notification }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadNotifications();
      }
    });
  }

  markAllAsRead(): void {

    const hasDigitalItemsPending = this.notifications.some(
      n => n.entity === 'DIGITAL_ITEM' && !n.read
    );

    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        // Update all notifications in local state
        this.notifications = this.notifications.map(n => {
          if (n.entity === 'GENERAL') {
            return { ...n, read: true };
          }
          return n;
        });

        if (hasDigitalItemsPending) {
          this.snackBar.open(
            'Se informa que las notificaciones de ítems digitales deben ser gestionadas manualmente.',
            'Ok',
            { duration: 5000, panelClass: ['warning-snackbar'] }
          );
        }
      },
      error: (err) => {
        console.error('Error marking all notifications as read:', err);
      }
    });
  }

  hasUnreadNotifications(): boolean {
    return this.notifications.some(n => !n.read);
  }

  getNotificationIcon(type?: string): string {
    switch (type) {
      case 'success':
        return 'check_circle';
      case 'warning':
        return 'warning';
      case 'error':
        return 'error';
      case 'info':
      default:
        return 'info';
    }
  }

  getNotificationColor(type?: string): string {
    switch (type) {
      case 'success':
        return 'success';
      case 'warning':
        return 'warning';
      case 'error':
        return 'error';
      case 'info':
      default:
        return 'info';
    }
  }

  getTypeLabel(type?: string): string {
    switch (type) {
      case 'success':
        return 'Éxito';
      case 'warning':
        return 'Advertencia';
      case 'error':
        return 'Error';
      case 'info':
      default:
        return 'Información';
    }
  }
}
