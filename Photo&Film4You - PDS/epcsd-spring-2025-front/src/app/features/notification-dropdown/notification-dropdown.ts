import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { NotificationService } from '@app/core/services/notification.service';
import { Notification } from '@app/core/models/notification';

@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule,
    MatListModule,
    MatDividerModule
  ],
  templateUrl: './notification-dropdown.html',
  styleUrls: ['./notification-dropdown.css']
})
export class NotificationDropdown {
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  notifications = computed(() => this.notificationService.notifications().slice(0, 5));
  unreadCount = computed(() => this.notificationService.unreadCount());

  onMenuOpened(): void {
    // Cargar todas las notificaciones cuando se abre el menú
    this.notificationService.getNotifications().subscribe({
      error: (err) => {
        console.error('Error loading notifications:', err);
      }
    });
  }

  markAsRead(notification: Notification, event: Event): void {
    event.stopPropagation();
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe({
        error: (err) => {
          console.error('Error marking notification as read:', err);
        }
      });
    }
  }

  handleNotificationClick(notification: Notification): void {
    if (notification.entity === 'DIGITAL_ITEM') {
      this.router.navigate(['/notifications']); // Redirección solicitada
    }
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      error: (err) => {
        console.error('Error marking all notifications as read:', err);
      }
    });
  }

  viewAllNotifications(): void {
    this.router.navigate(['/notifications']);
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
        return 'success-notification';
      case 'warning':
        return 'warning-notification';
      case 'error':
        return 'error-notification';
      case 'info':
      default:
        return 'info-notification';
    }
  }
}
