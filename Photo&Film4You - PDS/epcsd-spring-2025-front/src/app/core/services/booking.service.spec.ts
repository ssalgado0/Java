import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { BookingService } from './booking.service';
import { AuthService } from './auth.service';
import { environment } from '@app/environments/environment';
import { DateRange } from '@app/core/models/date/date-range';
import { AvailabilityRequest } from '@app/core/models/booking';

describe('BookingService', () => {
  let service: BookingService;
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  let store: { [key: string]: string } = {};
  let setItemSpy: jasmine.Spy;
  let removeItemSpy: jasmine.Spy;

  const mockStorage = {
    getItem: (key: string) => (key in store ? store[key] : null),
    setItem: (key: string, value: string) => (store[key] = `${value}`),
    removeItem: (key: string) => delete store[key],
    clear: () => (store = {}),
  };

  const safeSpy = (method: 'getItem' | 'setItem' | 'removeItem' | 'clear') => {
    if (!sessionStorage[method]) {
      (sessionStorage as any)[method] = () => {};
    }

    if (jasmine.isSpy(sessionStorage[method])) {
      const spy = sessionStorage[method] as jasmine.Spy;
      spy.and.callFake(mockStorage[method]);
      spy.calls.reset();
      return spy;
    } else {
      return spyOn(sessionStorage, method).and.callFake(mockStorage[method]);
    }
  };

  beforeEach(() => {
    store = {};

    safeSpy('getItem');
    safeSpy('clear');
    setItemSpy = safeSpy('setItem');
    removeItemSpy = safeSpy('removeItem');

    authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        BookingService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(BookingService);
    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('setDateRange should update subject and sessionStorage', () => {
    const range: DateRange = { start: new Date(2025, 0, 1), end: new Date(2025, 0, 5) };
    service.setDateRange(range);

    service.dateRange$.subscribe(val => {
      expect(val).toEqual(range);
    });
    expect(setItemSpy).toHaveBeenCalled();
  });

  it('resetDateRange should set null', () => {
    service.setDateRange({ start: new Date(), end: new Date() });
    service.resetDateRange();

    expect(service.getActualDateRange()).toBeNull();
    expect(removeItemSpy).toHaveBeenCalled();
  });

  it('should remove date range from storage if null is passed', () => {
    service.setDateRange(null);
    expect(removeItemSpy).toHaveBeenCalled();
    service.dateRange$.subscribe(range => expect(range).toBeNull());
  });


  it('should load date range from storage on initialization', () => {
    const validData = JSON.stringify({ start: new Date(), end: new Date() });
    store['booking_date_range'] = validData;


    const freshService = new BookingService(httpClient, authServiceSpy);

    freshService.dateRange$.subscribe(range => {
      expect(range).toBeTruthy();
    });
  });

  it('should handle corrupted JSON in session storage gracefully', () => {
    store['booking_date_range'] = '{ basura }';
    spyOn(console, 'warn');

    const freshService = new BookingService(httpClient, authServiceSpy);

    freshService.dateRange$.subscribe(range => {
      expect(range).toBeNull();
    });
    expect(removeItemSpy).toHaveBeenCalled();
  });

  it('should ignore storage if data is string "null" or "undefined"', () => {
    store['booking_date_range'] = 'null';

    const freshService = new BookingService(httpClient, authServiceSpy);

    freshService.dateRange$.subscribe(range => {
      expect(range).toBeNull();
    });
  });


  it('checkAvailability should call API correctly', () => {
    const request: AvailabilityRequest = {
        startDate: new Date(),
        endDate: new Date(),
        productIds: [1, 2]
    };
    const mockResponse = { available: true } as any;

    service.checkAvailability(request).subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bookings/availability`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });

  it('createBooking should call API with token and return booking', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');
    const bookingReq = { /* ... */ } as any;

    const mockBooking = {
      id: 123,
      userId: 1,
      startDate: '2025-12-20',
      endDate: '2025-12-25',
      status: 'PENDING',
      lines: [],
      allocations: []
    } as any;

    service.createBooking(bookingReq).subscribe(res => {
      expect(res).toEqual(mockBooking);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bookings`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    req.flush(mockBooking);
  });


  it('should handle request when token is missing', () => {
    authServiceSpy.getToken.and.returnValue(null);

    service.getUserBookings().subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/bookings`);

    expect(req.request.headers.get('Authorization')).toBe('Bearer ');

    req.flush([]);
  });
});
