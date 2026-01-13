import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '@app/environments/environment';
import {UserProfile} from '@app/core/models/user/user-profile.model';
import {AuthService} from '@app/core/services/auth.service';
import {UserRole} from '@app/core/enums/user-role.enum';

export interface CreateUserRequest {
  fullName: string;
  email: string;
  password: string;
  phoneNumber: string;
  role: UserRole;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly baseUrl = environment.apiUrl;

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();

    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  createUser(payload: CreateUserRequest): Observable<number> {
    const url = `${this.baseUrl}/users`;
    return this.http.post<number>(url, payload, {
      headers: this.getHeaders()
    });
  }

  getCurrentUser(): Observable<UserProfile> {
    const url = `${this.baseUrl}/users/me`;

    return this.http.get<UserProfile>(url, {
      headers: this.getHeaders()
    });
  }

  updateCurrentUser(user: UserProfile): Observable<UserProfile> {
    const url = `${this.baseUrl}/users/me`;
    return this.http.put<UserProfile>(url, user, {
      headers: this.getHeaders()
    });
  }

  changePassword(payload: { currentPassword: string; newPassword: string }): Observable<void> {
    const url = `${this.baseUrl}/users/me/change-password`;
    return this.http.post<void>(url, payload, {
      headers: this.getHeaders()
    });
  }

  deleteAccount(): Observable<void> {
    const url = `${this.baseUrl}/users/me`;
    return this.http.delete<void>(url, {
      headers: this.getHeaders()
    });
  }
}
