import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Notifications} from './notifications';
import {NotificationService} from '@app/core/services/notification.service';
import {of, throwError} from 'rxjs';
import {Notification} from '@app/core/models/notification';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';


describe('Notifications', () => {
  let component: Notifications;
  let fixture: ComponentFixture<Notifications>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  const mockNotifications: Notification[] = [
    {id: 1, title: 'Notif 1', message: 'Message 1', createdAt: new Date(), read: false, type: 'info', entity: 'GENERAL'},
    {id: 2, title: 'Notif 2', message: 'Message 2', createdAt: new Date(), read: true, type: 'success', entity: 'GENERAL'},
    {id: 3, title: 'Notif 3', message: 'Message 3', createdAt: new Date(), read: false, type: 'warning', entity: 'GENERAL'}
  ];

  beforeEach(async () => {
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['getNotifications', 'markAsRead', 'markAllAsRead']);

    await TestBed.configureTestingModule({
      imports: [Notifications],
      providers: [
        {provide: NotificationService, useValue: notificationServiceSpy},
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Notifications);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load notifications on init', () => {
    notificationServiceSpy.getNotifications.and.returnValue(of(mockNotifications));

    // triggers ngOnInit
    fixture.detectChanges();

    expect(notificationServiceSpy.getNotifications).toHaveBeenCalled();
    expect(component.notifications).toEqual(mockNotifications);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading notifications', () => {
    const error = new Error('Network error');
    notificationServiceSpy.getNotifications.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Error loading notifications:', error);
    expect(component.loading).toBeFalse();
  });

  it('should mark notification as read', () => {
    // Use a fresh copy for this test to avoid side effects
    const localMockNotifications = JSON.parse(JSON.stringify(mockNotifications));
    notificationServiceSpy.getNotifications.and.returnValue(of(localMockNotifications));
    fixture.detectChanges();

    // read: false
    const notificationToMark = component.notifications[0];
    notificationServiceSpy.markAsRead.and.returnValue(of(void 0));

    component.markAsRead(notificationToMark);

    expect(notificationServiceSpy.markAsRead).toHaveBeenCalledWith(notificationToMark.id);
    expect(notificationToMark.read).toBeTrue();
  });

  it('should not call service if notification is already read', () => {
    notificationServiceSpy.getNotifications.and.returnValue(of(mockNotifications));
    fixture.detectChanges();

    // read: true
    const readNotification = mockNotifications[1];
    component.markAsRead(readNotification);

    expect(notificationServiceSpy.markAsRead).not.toHaveBeenCalled();
  });

  it('should handle error when marking notification as read', () => {
    // Use a fresh copy for this test to avoid side effects
    const localMockNotifications = JSON.parse(JSON.stringify(mockNotifications));
    notificationServiceSpy.getNotifications.and.returnValue(of(localMockNotifications));
    fixture.detectChanges();

    const notificationToMark = component.notifications[0];
    const error = new Error('Update error');
    notificationServiceSpy.markAsRead.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.markAsRead(notificationToMark);

    expect(console.error).toHaveBeenCalledWith('Error marking notification as read:', error);
  });

  it('should mark all as read', () => {
    // Use a fresh copy for this test to avoid side effects
    const localMockNotifications = JSON.parse(JSON.stringify(mockNotifications));
    notificationServiceSpy.getNotifications.and.returnValue(of(localMockNotifications));
    fixture.detectChanges();

    notificationServiceSpy.markAllAsRead.and.returnValue(of(void 0));

    component.markAllAsRead();

    expect(notificationServiceSpy.markAllAsRead).toHaveBeenCalled();
    expect(component.notifications.every(n => n.read)).toBeTrue();
  });

  it('should check if there are unread notifications', () => {
    component.notifications = [
      {...mockNotifications[0], read: false},
      {...mockNotifications[1], read: true}
    ];
    expect(component.hasUnreadNotifications()).toBeTrue();

    component.notifications = [
      {...mockNotifications[0], read: true},
      {...mockNotifications[1], read: true}
    ];
    expect(component.hasUnreadNotifications()).toBeFalse();
  });

  it('should return correct icon for notification type', () => {
    expect(component.getNotificationIcon('success')).toBe('check_circle');
    expect(component.getNotificationIcon('warning')).toBe('warning');
    expect(component.getNotificationIcon('error')).toBe('error');
    expect(component.getNotificationIcon('info')).toBe('info');
    expect(component.getNotificationIcon(undefined)).toBe('info');
  });

  it('should return correct color for notification type', () => {
    expect(component.getNotificationColor('success')).toBe('success');
    expect(component.getNotificationColor('warning')).toBe('warning');
    expect(component.getNotificationColor('error')).toBe('error');
    expect(component.getNotificationColor('info')).toBe('info');
    expect(component.getNotificationColor(undefined)).toBe('info');
  });

  it('should return correct label for notification type', () => {
    expect(component.getTypeLabel('success')).toBe('Éxito');
    expect(component.getTypeLabel('warning')).toBe('Advertencia');
    expect(component.getTypeLabel('error')).toBe('Error');
    expect(component.getTypeLabel('info')).toBe('Información');
    expect(component.getTypeLabel(undefined)).toBe('Información');
  });
});
