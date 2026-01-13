import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationEditModal } from './notification-edit-modal';
import {DigitalItem} from '@app/core/models/digital';
import { Notification } from '@app/core/models/notification';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DigitalService} from '@app/core/services/digital.service';
import {NotificationService} from '@app/core/services/notification.service';
import {of, throwError} from 'rxjs';

describe('NotificationEditModal', () => {
  let component: NotificationEditModal;
  let fixture: ComponentFixture<NotificationEditModal>;


  const mockDigitalService = jasmine.createSpyObj('DigitalService', ['getDigitalItemById', 'approveDigitalItem', 'rejectDigitalItem']);
  const mockNotificationService = jasmine.createSpyObj('NotificationService', ['markAsRead']);
  const mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

  const mockNotification: Notification = {
    id: 101,
    title: 'Test Notification',
    message: 'Test Message with Digital ID "101"',
    read: false,
    createdAt: new Date('2024-01-01T10:00:00'),
    type: 'info',
    entity: 'DIGITAL_ITEM'
  };

  const mockDigitalItem: DigitalItem = {
    id: 101,
    digitalSessionId: 1,
    description: 'Digital Item Description',
    lat: 123,
    lon: 123,
    link: 'http://test.com',
    status: DigitalStatus.AVAILABLE
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationEditModal],
      providers: [
        { provide: DigitalService, useValue: mockDigitalService },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: { notification: mockNotification } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationEditModal);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create and load digital item details on init', () => {
    mockDigitalService.getDigitalItemById.and.returnValue(of(mockDigitalItem));

    fixture.detectChanges(); // Ejecuta ngOnInit

    expect(mockDigitalService.getDigitalItemById).toHaveBeenCalledWith(101);
    expect(component.item()).toEqual(mockDigitalItem);
    expect(component.loading()).toBeFalse();
  });

  it('should call approveDigitalItem and mark notification as read when updateStatus(true) is called', () => {
    // GIVEN
    component.item.set(mockDigitalItem);
    mockDigitalService.approveDigitalItem.and.returnValue(of(void 0));
    mockNotificationService.markAsRead.and.returnValue(of(void 0));

    // WHEN
    component.updateStatus(true);

    // THEN
    expect(mockDigitalService.approveDigitalItem).toHaveBeenCalledWith(101);
    expect(mockNotificationService.markAsRead).toHaveBeenCalledWith(101 as any);
    expect(mockDialogRef.close).toHaveBeenCalledWith(true as any);
  });

  it('should call rejectDigitalItem and mark notification as read when updateStatus(false) is called', () => {
    // GIVEN
    component.item.set(mockDigitalItem);
    mockDigitalService.rejectDigitalItem.and.returnValue(of(void 0));
    mockNotificationService.markAsRead.and.returnValue(of(void 0));

    // WHEN
    component.updateStatus(false);

    // THEN
    expect(mockDigitalService.rejectDigitalItem).toHaveBeenCalledWith(101);
    expect(mockNotificationService.markAsRead).toHaveBeenCalledWith(101 as any);
    expect(mockDialogRef.close).toHaveBeenCalledWith(true as any);
  });

  it('should handle errors when loading digital item fails', () => {
    mockDigitalService.getDigitalItemById.and.returnValue(throwError(() => new Error('API Error')));

    fixture.detectChanges();

    expect(component.loading()).toBeFalse();
    expect(component.item()).toBeNull();
  });
});
