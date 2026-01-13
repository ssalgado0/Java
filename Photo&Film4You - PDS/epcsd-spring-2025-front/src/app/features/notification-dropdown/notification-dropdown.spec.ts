import {ComponentFixture, TestBed} from '@angular/core/testing';
import {NotificationDropdown} from './notification-dropdown';
import {NotificationService} from '@app/core/services/notification.service';
import {Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {Notification} from '@app/core/models/notification';
import {signal} from '@angular/core';

describe('NotificationDropdown', () => {
  let component: NotificationDropdown;
  let fixture: ComponentFixture<NotificationDropdown>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockNotifications: Notification[] = [
    {id: 1, title: 'Notif 1', message: 'Message 1', createdAt: new Date(), read: false, type: 'info', entity: 'GENERAL'},
    {id: 2, title: 'Notif 2', message: 'Message 2', createdAt: new Date(), read: true, type: 'success', entity: 'GENERAL'},
    {id: 3, title: 'Notif 3', message: 'Message 3', createdAt: new Date(), read: false, type: 'warning', entity: 'GENERAL'},
    {id: 4, title: 'Notif 4', message: 'Message 4', createdAt: new Date(), read: false, type: 'error', entity: 'GENERAL'},
    {id: 5, title: 'Notif 5', message: 'Message 5', createdAt: new Date(), read: true, type: 'info', entity: 'GENERAL'},
    {id: 6, title: 'Notif 6', message: 'Message 6', createdAt: new Date(), read: false, type: 'success', entity: 'GENERAL'}
  ];

  beforeEach(async () => {
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['getNotifications', 'markAsRead', 'markAllAsRead'], {
      notifications: signal(mockNotifications),
      unreadCount: signal(3)
    });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [NotificationDropdown],
      providers: [
        {provide: NotificationService, useValue: notificationServiceSpy},
        {provide: Router, useValue: routerSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display only first 5 notifications', () => {
    expect(component.notifications().length).toBe(5);
    expect(component.notifications()).toEqual(mockNotifications.slice(0, 5));
  });

  it('should display unread count', () => {
    expect(component.unreadCount()).toBe(3);
  });

  it('should load notifications when menu is opened', () => {
    notificationServiceSpy.getNotifications.and.returnValue(of(mockNotifications));

    component.onMenuOpened();

    expect(notificationServiceSpy.getNotifications).toHaveBeenCalled();
  });

  it('should handle error when loading notifications on menu open', () => {
    const error = new Error('Network error');
    notificationServiceSpy.getNotifications.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.onMenuOpened();

    expect(console.error).toHaveBeenCalledWith('Error loading notifications:', error);
  });

  it('should mark notification as read', () => {
    const notificationToMark = mockNotifications[0]; // read: false
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');
    notificationServiceSpy.markAsRead.and.returnValue(of(void 0));

    component.markAsRead(notificationToMark, event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(notificationServiceSpy.markAsRead).toHaveBeenCalledWith(notificationToMark.id);
  });

  it('should not call service if notification is already read', () => {
    const readNotification = mockNotifications[1]; // read: true
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.markAsRead(readNotification, event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(notificationServiceSpy.markAsRead).not.toHaveBeenCalled();
  });

  it('should handle error when marking notification as read', () => {
    const notificationToMark = mockNotifications[0];
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');
    const error = new Error('Update error');
    notificationServiceSpy.markAsRead.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.markAsRead(notificationToMark, event);

    expect(console.error).toHaveBeenCalledWith('Error marking notification as read:', error);
  });

  it('should mark all as read', () => {
    notificationServiceSpy.markAllAsRead.and.returnValue(of(void 0));

    component.markAllAsRead();

    expect(notificationServiceSpy.markAllAsRead).toHaveBeenCalled();
  });

  it('should handle error when marking all as read', () => {
    const error = new Error('Update error');
    notificationServiceSpy.markAllAsRead.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.markAllAsRead();

    expect(console.error).toHaveBeenCalledWith('Error marking all notifications as read:', error);
  });

  it('should navigate to notifications page', () => {
    component.viewAllNotifications();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/notifications']);
  });

  it('should return correct icon for notification type', () => {
    expect(component.getNotificationIcon('success')).toBe('check_circle');
    expect(component.getNotificationIcon('warning')).toBe('warning');
    expect(component.getNotificationIcon('error')).toBe('error');
    expect(component.getNotificationIcon('info')).toBe('info');
    expect(component.getNotificationIcon(undefined)).toBe('info');
  });

  it('should return correct color class for notification type', () => {
    expect(component.getNotificationColor('success')).toBe('success-notification');
    expect(component.getNotificationColor('warning')).toBe('warning-notification');
    expect(component.getNotificationColor('error')).toBe('error-notification');
    expect(component.getNotificationColor('info')).toBe('info-notification');
    expect(component.getNotificationColor(undefined)).toBe('info-notification');
  });
});
