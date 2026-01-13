import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute, convertToParamMap, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';

import {of} from 'rxjs';

import {BookingSummary} from './booking-summary';
import {BookingService} from '@app/core/services/booking.service';
import {Booking, BookingStatus} from '@app/core/models/booking/booking.model';

describe('BookingSummary', () => {
  let component: BookingSummary;
  let fixture: ComponentFixture<BookingSummary>;
  let routerMock: jasmine.SpyObj<Router>;
  let bookingServiceMock: jasmine.SpyObj<BookingService>;

  const mockBooking: Booking = {
    id: 1,
    userId: 1,
    startDate: '2025-12-20',
    endDate: '2025-12-25',
    status: BookingStatus.PENDING,
    lines: [
      {
        id: 1,
        quantity: 2,
        product: {
          id: 1,
          name: 'Producto 1',
          dailyPrice: 10
        } as any,
        pricePerUnit: '10',
        totalPrice: '20'
      },
      {
        id: 2,
        quantity: 1,
        product: {
          id: 2,
          name: 'Producto 2',
          dailyPrice: 20
        } as any,
        pricePerUnit: '20',
        totalPrice: '20'
      }
    ],
    allocations: []
  };

  beforeEach(async () => {
    routerMock = jasmine.createSpyObj<Router>('Router', ['navigate']);
    bookingServiceMock = jasmine.createSpyObj<BookingService>('BookingService', ['getBookingById']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        BookingSummary
      ],
      providers: [
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '1' })
            }
          }
        },
        { provide: BookingService, useValue: bookingServiceMock }
      ]
    }).compileComponents();
  });

  function createComponentWithBooking() {
    bookingServiceMock.getBookingById.and.returnValue(of(mockBooking));

    fixture = TestBed.createComponent(BookingSummary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and load booking when id route param exists', () => {
    createComponentWithBooking();

    expect(component).toBeTruthy();
    expect(component.booking).toEqual(mockBooking);
    expect(component.dataSource.data.length).toBe(mockBooking.lines.length);
    expect(component.totalCost).toBe(40); // 2*10 + 1*20
  });

  it('goBackToProducts should navigate to /products', () => {
    createComponentWithBooking();

    component.goBackToProducts();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/products']);
  });

});
