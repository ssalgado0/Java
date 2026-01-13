import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '@app/environments/environment';
import {Observable, tap} from 'rxjs';
import {CurrentUser, LoginResponse} from '../models/auth';
import {jwtDecode} from 'jwt-decode';
import {UserRole} from '@app/core/enums/user-role.enum';
import {LoginRequest} from '@app/core/models/auth/login-request.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = environment.apiUrl;
  private readonly TOKEN_KEY = 'jwt_token';
  currentUser = signal<CurrentUser | null>(null);

  constructor(private readonly http: HttpClient) {
    const token = sessionStorage.getItem(this.TOKEN_KEY);
    if (token) this.setUserFromToken(token);
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const url = `${this.baseUrl}/auth/login`;
    const request: LoginRequest = {email, password};

    return this.http.post<LoginResponse>(url, request).pipe(
      tap((res) => {
        sessionStorage.setItem(this.TOKEN_KEY, res.token);
        this.setUserFromToken(res.token);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    this.currentUser.set(null);
  }

  private setUserFromToken(token: string): void {
    const decoded: any = jwtDecode(token);
    const user: CurrentUser = {
      id: decoded.jti,
      email: decoded.sub,
      fullName: decoded.fullName,
      role: decoded.role
    };
    this.currentUser.set(user);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.currentUser();
  }

  hasRole(roles: UserRole[]): boolean {
    const user = this.currentUser();
    return user != null && roles.includes(user.role);
  }
}
