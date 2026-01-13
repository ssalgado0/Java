import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '@app/environments/environment';
import {interval, Observable, switchMap, tap} from 'rxjs';
import {Notification} from '@app/core/models/notification';
import {AuthService} from '@app/core/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly baseUrl = environment.apiUrl;
  private readonly POLLING_INTERVAL = 10000; // 60 seconds

  // Signal para las notificaciones
  notifications = signal<Notification[]>([]);
  unreadCount = signal<number>(0);

  constructor(private readonly http: HttpClient, private readonly authService: AuthService) {
    // Iniciar el polling automático
    this.startPolling();
  }

  /**
   * Obtiene todas las notificaciones
   */
  getNotifications(): Observable<Notification[]> {
    const url = `${this.baseUrl}/notifications`;
    const token = this.authService.getToken();

    return this.http.get<Notification[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    }).pipe(
      tap((notifications) => {
        this.notifications.set(notifications);
        this.updateUnreadCount(notifications);
      })
    );
  }

  /**
   * Obtiene solo las notificaciones no leídas
   */
  getUnreadNotifications(): Observable<Notification[]> {
    const url = `${this.baseUrl}/notifications/unread`;
    const token = this.authService.getToken();

    return this.http.get<Notification[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    }).pipe(
      tap((unreadNotifications) => {
        // Actualizamos el contador de no leídas
        this.unreadCount.set(unreadNotifications.length);
      })
    );
  }

  /**
   * Marca una notificación como leída
   */
  markAsRead(notificationId: number): Observable<void> {
    const url = `${this.baseUrl}/notifications/${notificationId}/read`;
    const token = this.authService.getToken();

    return this.http.patch<void>(url, {}, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    }).pipe(
      tap(() => {
        // Actualizar el estado local
        const updated = this.notifications().map((n) =>
          n.id === notificationId ? { ...n, read: true } : n
        );
        this.notifications.set(updated);
        this.updateUnreadCount(updated);
      })
    );
  }

  /**
   * Marca todas las notificaciones como leídas
   */
  markAllAsRead(): Observable<void> {
    const url = `${this.baseUrl}/notifications/read-all`;
    const token = this.authService.getToken();

    return this.http.patch<void>(url, {}, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    }).pipe(
      tap(() => {
        // Actualizar el estado local
        const updated = this.notifications().map((n) => ({ ...n, read: true }));
        this.notifications.set(updated);
        this.unreadCount.set(0);
      })
    );
  }

  /**
   * Inicia el polling automático cada 60 segundos
   */
  private startPolling(): void {
    interval(this.POLLING_INTERVAL)
      .pipe(
        switchMap(() => this.getUnreadNotifications())
      )
      .subscribe({
        next: () => {
          // El contador de no leídas se actualiza automáticamente en el tap
        },
        error: (err) => {
          console.error('Error fetching notifications:', err);
        }
      });

    // Cargar contador inicial de notificaciones no leídas
    this.getUnreadNotifications().subscribe();
  }

  /**
   * Actualiza el contador de notificaciones no leídas
   */
  private updateUnreadCount(notifications: Notification[]): void {
    const unread = notifications.filter((n) => !n.read).length;
    this.unreadCount.set(unread);
  }

  /**
   * Método público para forzar la actualización de notificaciones
   */
  refresh(): void {
    this.getUnreadNotifications().subscribe();
  }
}
