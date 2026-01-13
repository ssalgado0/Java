import {discardPeriodicTasks, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {NotificationService} from './notification.service';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '@app/core/services/auth.service';
import {of} from 'rxjs';
import {Notification} from '@app/core/models/notification';
import {environment} from '@app/environments/environment';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: jasmine.SpyObj<HttpClient>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  const apiUrl = environment.apiUrl;

  const mockNotifications: Notification[] = [
    {id: 1, title: 'N1', message: 'M1', createdAt: new Date(), read: false, type: 'info', entity: 'GENERAL'},
    {id: 2, title: 'N2', message: 'M2', createdAt: new Date(), read: true, type: 'success', entity: 'GENERAL'}
  ];

  beforeEach(() => {
    httpMock = jasmine.createSpyObj('HttpClient', ['get', 'patch']);
    authServiceMock = jasmine.createSpyObj('AuthService', ['getToken']);

    httpMock.get.and.returnValue(of([]));
    authServiceMock.getToken.and.returnValue('mock-token');

    TestBed.configureTestingModule({
      providers: [
        NotificationService,
        {provide: HttpClient, useValue: httpMock},
        {provide: AuthService, useValue: authServiceMock}
      ]
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getNotifications should fetch notifications and update signal', () => {
    httpMock.get.and.returnValue(of(mockNotifications));

    service.getNotifications().subscribe(notifications => {
      expect(notifications).toEqual(mockNotifications);
      expect(service.notifications()).toEqual(mockNotifications);

      // One unread notification
      expect(service.unreadCount()).toBe(1);
    });

    expect(httpMock.get).toHaveBeenCalledWith(`${apiUrl}/notifications`, jasmine.any(Object));
  });

  it('getUnreadNotifications should fetch unread notifications and update count', () => {
    const unreadNotifications = [mockNotifications[0]];
    httpMock.get.and.returnValue(of(unreadNotifications));

    service.getUnreadNotifications().subscribe(notifications => {
      expect(notifications).toEqual(unreadNotifications);
      expect(service.unreadCount()).toBe(1);
    });

    expect(httpMock.get).toHaveBeenCalledWith(`${apiUrl}/notifications/unread`, jasmine.any(Object));
  });

  it('markAsRead should patch notification and update local state', () => {
    service.notifications.set(mockNotifications);
    service.unreadCount.set(1);

    httpMock.patch.and.returnValue(of(void 0));

    const notificationId = 1;
    service.markAsRead(notificationId).subscribe(() => {
      const updatedNotification = service.notifications().find(n => n.id === notificationId);
      expect(updatedNotification?.read).toBeTrue();
      expect(service.unreadCount()).toBe(0);
    });

    expect(httpMock.patch).toHaveBeenCalledWith(`${apiUrl}/notifications/${notificationId}/read`, {}, jasmine.any(Object));
  });

  it('markAllAsRead should patch all notifications and update local state', () => {
    service.notifications.set(mockNotifications);
    service.unreadCount.set(1);

    httpMock.patch.and.returnValue(of(void 0));

    service.markAllAsRead().subscribe(() => {
      const allRead = service.notifications().every(n => n.read);
      expect(allRead).toBeTrue();
      expect(service.unreadCount()).toBe(0);
    });

    expect(httpMock.patch).toHaveBeenCalledWith(`${apiUrl}/notifications/read-all`, {}, jasmine.any(Object));
  });

  it('refresh should call getUnreadNotifications', () => {
    spyOn(service, 'getUnreadNotifications').and.returnValue(of([]));
    service.refresh();
    expect(service.getUnreadNotifications).toHaveBeenCalled();
  });

  it('should poll for unread notifications', fakeAsync(() => {
    // Reset TestBed to ensure we create the service inside the fakeAsync zone
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [
        NotificationService,
        {provide: HttpClient, useValue: httpMock},
        {provide: AuthService, useValue: authServiceMock}
      ]
    });

    // Inject service inside fakeAsync to capture the interval
    const localService = TestBed.inject(NotificationService);

    // Reset calls from constructor (initial fetch)
    httpMock.get.calls.reset();

    // Mock getUnreadNotifications response
    httpMock.get.and.returnValue(of([]));

    // Fast-forward time by POLLING_INTERVAL (10000ms)
    tick(10000);

    expect(httpMock.get).toHaveBeenCalledWith(`${apiUrl}/notifications/unread`, jasmine.any(Object));

    discardPeriodicTasks();
  }));
});
