import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Rent} from './rent';
import {BookingService} from '@app/core/services/booking.service';
import {LoadingService} from '@app/core/services/loading-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {of, throwError} from 'rxjs';
import {Booking} from '@app/core/models/booking/booking.model';

describe('Rent Component', () => {
  let component: Rent;
  let fixture: ComponentFixture<Rent>;

  let bookingServiceSpy: jasmine.SpyObj<BookingService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockBookings: Booking[] = [
    {
      id: 1,
      userId: 100,
      allocations: [],
      startDate: new Date().toISOString(),
      endDate: new Date().toISOString(),
      status: 'CONFIRMED' as any,
      lines: [
        {
          id: 101,
          quantity: 2,
          product: {
            id: 1,
            name: 'Camara',
            dailyPrice: 50,
            brand: 'Sony',
            categoryId: 1,
            description: 'Desc',
            model: 'A1'
          },
          pricePerUnit: '50',
          totalPrice: '100'
        },
        {
          id: 102,
          quantity: 1,
          product: {
            id: 2,
            name: 'Trípode',
            dailyPrice: 20,
            brand: 'Manfrotto',
            categoryId: 2,
            description: 'Desc',
            model: 'X'
          },
          pricePerUnit: '20',
          totalPrice: '20'
        }
      ]
    }
  ];

  beforeEach(async () => {
    bookingServiceSpy = jasmine.createSpyObj('BookingService', ['getUserBookings']);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    bookingServiceSpy.getUserBookings.and.returnValue(of(mockBookings));

    await TestBed.configureTestingModule({
      imports: [Rent],
      providers: [
        { provide: BookingService, useValue: bookingServiceSpy },
        { provide: LoadingService, useValue: loadingServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Rent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load bookings and calculate total price correctly', () => {
    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(component.bookings.length).toBe(1);

    const booking = component.bookings[0];
    expect(booking.totalPrice).toBe(120);

    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  });

  it('should handle null response from service gracefully', () => {
    bookingServiceSpy.getUserBookings.and.returnValue(of(null as any));

    component.getUserBookings();

    expect(component.bookings).toEqual([]);
    expect(component.isLoading).toBeFalse();
  });

  it('should handle error when loading bookings fails', () => {
    bookingServiceSpy.getUserBookings.and.returnValue(throwError(() => new Error('Server error')));
    spyOn(console, 'error');

    component.getUserBookings();

    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Error al cargar reservas",
      "Cerrar",
      jasmine.any(Object)
    );
  });

  it('should calculate total as 0 or ignore lines with missing price/quantity', () => {
    const weirdBookings: Booking[] = [{
      id: 2,
      userId: 100,
      allocations: [],
      startDate: new Date().toISOString(),
      endDate: new Date().toISOString(),
      status: 'PENDING' as any,
      lines: [
        {id: 201, quantity: 2, product: {dailyPrice: undefined} as any, pricePerUnit: '0', totalPrice: '0'},
        {id: 202, quantity: 5, product: null as any, pricePerUnit: '0', totalPrice: '0'},
        {id: 203, quantity: undefined as any, product: {dailyPrice: 100} as any, pricePerUnit: '0', totalPrice: '0'}
      ]
    }];

    bookingServiceSpy.getUserBookings.and.returnValue(of(weirdBookings));

    component.getUserBookings();

    expect(component.bookings[0].totalPrice).toBe(0);
  });
});
