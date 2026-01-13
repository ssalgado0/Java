import { Injectable } from '@angular/core';
import { DateRange } from '@app/core/models/date/date-range';
import { environment } from '@app/environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { AvailabilityRequest, AvailabilityResponse } from '@app/core/models/booking';
import { BookingRequest } from '@app/core/models/booking/booking-request.model';
import { Booking } from '@app/core/models/booking/booking.model';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  private readonly baseUrl = environment.apiUrl;

  dateRange$ = new Observable<DateRange | null>();
  private readonly dateRangeSource = new BehaviorSubject<DateRange | null>(null);
  private readonly dateRangeStorageKey = 'booking_date_range';

  constructor(private readonly http: HttpClient, private readonly authService: AuthService) {
    this.dateRange$ = this.dateRangeSource.asObservable();
    this.loadFromStorage();
  }

  setDateRange(range: DateRange | null): void {
    this.dateRangeSource.next(range);
    if (range) {
      sessionStorage.setItem(this.dateRangeStorageKey, JSON.stringify(range));
    } else {
      sessionStorage.removeItem(this.dateRangeStorageKey);
    }
  }

  resetDateRange(): void {
    this.dateRangeSource.next(null);
    sessionStorage.removeItem(this.dateRangeStorageKey);
  }

  getActualDateRange(): DateRange | null {
    return this.dateRangeSource.getValue();
  }

  checkAvailability(availabilityRequest: AvailabilityRequest): Observable<AvailabilityResponse> {
    const url = `${this.baseUrl}/bookings/availability`;
    return this.http.post<AvailabilityResponse>(url, availabilityRequest);
  }

  createBooking(bookingRequest: BookingRequest): Observable<Booking> {
    const url = `${this.baseUrl}/bookings`;
    const token = this.authService.getToken();
    return this.http.post<Booking>(url, bookingRequest, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }

  getUserBookings(): Observable<Booking[]> {
    const url = `${this.baseUrl}/bookings`;
    const token = this.authService.getToken();

    return this.http.get<Booking[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }

  private loadFromStorage(): void {
    const data = sessionStorage.getItem(this.dateRangeStorageKey);
    if (data && data !== 'null' && data !== 'undefined') {
      try {
        const range: DateRange = JSON.parse(data);
        if (range?.start && range?.end) {
        this.dateRangeSource.next({ start: new Date(range.start), end: new Date(range.end) });
        }
      } catch (error) {
        console.warn('Error parsing date range', error);
        sessionStorage.removeItem(this.dateRangeStorageKey);
      }
    }
  }

  getBookingById(id: number): Observable<Booking> {
    const url = `${this.baseUrl}/bookings/${id}`;
    const token = this.authService.getToken();

    return this.http.get<Booking>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }
}
