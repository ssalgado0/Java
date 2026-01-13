import {Injectable} from '@angular/core';
import {AuthService} from '@app/core/services/auth.service';
import {Observable} from 'rxjs';
import {DigitalItem, DigitalSession} from '@app/core/models/digital';
import {environment} from '@app/environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class DigitalService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient, private readonly authService: AuthService) {
  }

  getUserDigitalSessions(): Observable<DigitalSession[]> {
    const url = `${this.baseUrl}/digital`;
    const token = this.authService.getToken();

    return this.http.get<DigitalSession[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }

  getAllDigitalSessions(): Observable<DigitalSession[]> {
    const url = `${this.baseUrl}/digital/allDigital`;
    const token = this.authService.getToken();

    return this.http.get<DigitalSession[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }

  getDigitalSessionById(id: number): Observable<DigitalSession> {
    const url = `${this.baseUrl}/digital/${id}`;
    const token = this.authService.getToken();

    return this.http.get<DigitalSession>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
    });
  }

  approveDigitalItem(id: number): Observable<void> {
    const url = `${this.baseUrl}/digitalItem/approveDigitalItem/${id}`;
    return this.http.patch<void>(url, {}, { headers: this.getHeaders() });
  }

  rejectDigitalItem(id: number): Observable<void> {
    const url = `${this.baseUrl}/digitalItem/rejectDigitalItem/${id}`;
    return this.http.patch<void>(url, {}, { headers: this.getHeaders() });
  }

  private getHeaders() {
    const token = this.authService.getToken();
    return {
      Authorization: `Bearer ${token || ''}`
    };
  }

  createDigitalSession(description: string, email: string): Observable<number> {
    const url = `${this.baseUrl}/digital/createDigital`;
    const token = this.authService.getToken();

    const request: { description: string; email: string } = {description, email}

    return this.http.post<number>(url, request, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  updateDigitalSession(id: number, description: string, email: string): Observable<boolean> {
    const url = `${this.baseUrl}/digital/updateDigital/${id}`;
    const token = this.authService.getToken();

    const request: { description: string; email: string } = {description, email}

    return this.http.put<boolean>(url, request, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  deleteDigitalSession(id: number): Observable<boolean> {
    const url = `${this.baseUrl}/digital/removeDigital/${id}`;
    const token = this.authService.getToken();

    return this.http.delete<boolean>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });

  }

  countDigitalItemsBySessionId(id: number): Observable<number> {
    const url = `${this.baseUrl}/digitalItem/digitalItemBySession`;
    const token = this.authService.getToken();

    return this.http.get<number>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
      params: {
        digitalSessionId: id,
        count: true
      }
    });
  }

  getDigitalItemsBySessionId(id: number): Observable<DigitalItem[]> {
    const url = `${this.baseUrl}/digitalItem/digitalItemBySession`;
    const token = this.authService.getToken();

    return this.http.get<DigitalItem[]>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      },
      params: {
        digitalSessionId: id
      }
    });
  }

  getDigitalItemById(id: number): Observable<DigitalItem> {
    const url = `${this.baseUrl}/digitalItem/${id}`;
    const token = this.authService.getToken();

    return this.http.get<DigitalItem>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  addDigitalItem(item: DigitalItem): Observable<number> {
    const url = `${this.baseUrl}/digitalItem/addItem`;
    const token = this.authService.getToken();

    return this.http.post<number>(url, item, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  updateDigitalItem(id: number, item: DigitalItem): Observable<boolean> {
    const url = `${this.baseUrl}/digitalItem/updateItem/${id}`;
    const token = this.authService.getToken();

    return this.http.put<boolean>(url, item, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }

  deleteDigitalItem(id: number): Observable<boolean> {
    const url = `${this.baseUrl}/digitalItem/dropItem/${id}`;
    const token = this.authService.getToken();

    return this.http.delete<boolean>(url, {
      headers: {
        Authorization: `Bearer ${token || ''}`
      }
    });
  }
}
