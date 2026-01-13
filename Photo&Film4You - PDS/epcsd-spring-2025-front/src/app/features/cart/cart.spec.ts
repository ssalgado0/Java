import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cart } from './cart';
import { BookingCart, CartItem } from '@app/core/models/cart';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { CartService } from '@app/core/services/cart.service';
import { Product } from '@app/core/models/product/product.model';
import { provideRouter } from '@angular/router';
import { BookingService } from '@app/core/services/booking.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DateRange } from '@app/core/models/date/date-range';
import { AvailabilityResponse } from '@app/core/models/booking';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Booking, BookingStatus } from '@app/core/models/booking/booking.model';

describe('Cart Component', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;

  let mockCartSubject: BehaviorSubject<BookingCart>;
  let mockDateRangeSubject: BehaviorSubject<DateRange | null>;

  let cartServiceSpy: jasmine.SpyObj<CartService>;
  let bookingServiceSpy: jasmine.SpyObj<BookingService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockProduct1: Product = {
    id: 1,
    name: 'Cámara Sony',
    dailyPrice: 100,
    brand: 'Sony',
    categoryId: 1,
    description: 'Camara pro',
    model: 'Alpha'
  };

  const mockProduct2: Product = {
    id: 2,
    name: 'Trípode',
    dailyPrice: 50,
    brand: 'Manfrotto',
    categoryId: 2,
    description: 'Tripode estable',
    model: 'XPRO'
  };

  const mockCartData: BookingCart = new BookingCart();
  mockCartData.items = [
    { product: mockProduct1, quantity: 2 } as CartItem,
    { product: mockProduct2, quantity: 1 } as CartItem
  ];

  const mockDateRange: DateRange = {
    start: new Date(),
    end: new Date(new Date().setDate(new Date().getDate() + 5))
  };

  const mockAvailabilityResponse: AvailabilityResponse = {
    startDate: new Date(),
    endDate: new Date(),
    availableUnitsByProduct: {
      1: 10,
      2: 2
    }
  };

  const mockBooking: Booking = {
    id: 123,
    userId: 1,
    startDate: '2025-12-20',
    endDate: '2025-12-25',
    status: BookingStatus.PENDING,
    lines: [],
    allocations: []
  };

  beforeEach(async () => {
    mockCartSubject = new BehaviorSubject<BookingCart>(new BookingCart());
    mockDateRangeSubject = new BehaviorSubject<DateRange | null>(null);

    cartServiceSpy = jasmine.createSpyObj('CartService', ['addProduct', 'removeProduct', 'clear']);
    Object.defineProperty(cartServiceSpy, 'cart$', { value: mockCartSubject.asObservable() });

    bookingServiceSpy = jasmine.createSpyObj('BookingService', ['checkAvailability', 'createBooking', 'getActualDateRange']);
    Object.defineProperty(bookingServiceSpy, 'dateRange$', { value: mockDateRangeSubject.asObservable() });

    bookingServiceSpy.checkAvailability.and.returnValue(of(mockAvailabilityResponse));
    // ⬇️ ahora devuelve un Booking, no un número
    bookingServiceSpy.createBooking.and.returnValue(of(mockBooking));

    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [Cart],
      providers: [
        { provide: CartService, useValue: cartServiceSpy },
        { provide: BookingService, useValue: bookingServiceSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load items and check availability on init when date range is present', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.items.length).toBe(2);
    expect(bookingServiceSpy.checkAvailability).toHaveBeenCalled();
  });

  it('should not load items if date range is missing', () => {
    mockDateRangeSubject.next(null);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.items.length).toBe(0);
  });

  it('should calculate the total cost correctly', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    const total = component.getTotalCost();
    expect(total).toBe(250);
  });

  it('should handle empty cart gracefully', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(new BookingCart());
    fixture.detectChanges();

    const total = component.getTotalCost();
    expect(component.items.length).toBe(0);
    expect(total).toBe(0);
  });

  it('should call service.addProduct with quantity 1 when increase is clicked', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    if (component.items.length > 0) {
        const item = component.items[0];
        component.increase(item);
        expect(cartServiceSpy.addProduct).toHaveBeenCalledWith(item.product, 1);
    } else {
        fail('No items in cart to increase');
    }
  });

  it('should call service.removeProduct with quantity 1 when decrease is clicked', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    if (component.items.length > 0) {
        const item = component.items[0];
        component.decrease(item);
        expect(cartServiceSpy.removeProduct).toHaveBeenCalledWith(item.product, 1);
    }
  });

  it('should call service.removeProduct (without quantity) when remove is clicked', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    if (component.items.length > 0) {
        const item = component.items[0];
        component.remove(item);
        expect(cartServiceSpy.removeProduct).toHaveBeenCalledWith(item.product);
    }
  });

  it('should call service.clear when clearCart is executed', () => {
    component.clearCart();
    expect(cartServiceSpy.clear).toHaveBeenCalled();
  });

  it('should handle availability check success', () => {
    bookingServiceSpy.checkAvailability.and.returnValue(of(mockAvailabilityResponse));

    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.availabilityStatus).toEqual(mockAvailabilityResponse);
  });

  it('should handle availability check error', () => {
    bookingServiceSpy.checkAvailability.and.returnValue(throwError(() => new Error('Error')));

    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.availabilityStatus).toBeNull();
    expect(snackBarSpy.open).toHaveBeenCalled();
  });

  it('should return correct availability status string', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.items.length).toBeGreaterThan(0);

    const itemHighStock = component.items[0];
    const itemLowStock = component.items[1];

    expect(component.getAvailabilityStatus(itemHighStock)).toBe('bg-success');
    expect(component.getAvailabilityStatus(itemLowStock)).toBe('bg-warning text-dark');
  });

  it('should return danger status when stock is 0', () => {
    const responseWithZero: AvailabilityResponse = {
      startDate: new Date(),
      endDate: new Date(),
      availableUnitsByProduct: { 1: 0, 2: 0 }
    };
    bookingServiceSpy.checkAvailability.and.returnValue(of(responseWithZero));

    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    expect(component.getAvailabilityStatus(component.items[0])).toBe('bg-danger');
  });

  it('should check if it is possible to add items correctly', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    fixture.detectChanges();

    const itemPossible = component.items[0];
    const itemNotPossible = { ...component.items[0], quantity: 20 };

    expect(component.isPossibleToAddItem(itemPossible)).toBeTrue();
    expect(component.isPossibleToAddItem(itemNotPossible)).toBeFalse();
  });

  it('should execute checkout logic', () => {
    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(mockCartData);
    bookingServiceSpy.createBooking.and.returnValue(of(mockBooking));

    fixture.detectChanges();

    component.checkout();

    expect(bookingServiceSpy.createBooking).toHaveBeenCalled();
    expect(cartServiceSpy.clear).toHaveBeenCalled();
  });

  it('initialization should load cart data correctly', () => {
    const testCartData = new BookingCart();
    testCartData.items = [{ product: mockProduct1, quantity: 1 } as CartItem];

    mockDateRangeSubject.next(mockDateRange);
    mockCartSubject.next(testCartData);

    fixture.detectChanges();

    expect(component.items.length).toBe(1);
    expect(component.items[0].product.name).toBe('Cámara Sony');
  });
});
