import {TestBed} from '@angular/core/testing';
import {AuthService} from './auth.service';
import {HttpClient} from '@angular/common/http';
import {of} from 'rxjs';
import {provideRouter} from '@angular/router';
import {CurrentUser} from '@app/core/models/auth';
import {UserRole} from '@app/core/enums/user-role.enum';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: jasmine.SpyObj<HttpClient>;
  let sessionStorageMock: any;

  const TOKEN =
    'eyJhbGciOiJub25lIn0.eyJqdGkiOiIxMjMiLCJzdWIiOiJ1c2VyQHRlc3QuY29tIiwiZnVsbE5hbWUiOiJKYXZpIFRlc3QiLCJyb2xlIjoiQURNSU4ifQ.';

  const EXPECTED_USER = {
    id: '123',
    email: 'user@test.com',
    fullName: 'Javi Test',
    role: UserRole.ADMIN,
  };

  beforeEach(() => {
    httpMock = jasmine.createSpyObj('HttpClient', ['post']);
    TestBed.overrideProvider(HttpClient, { useValue: httpMock });

    sessionStorageMock = {
      storage: {} as Record<string, string>,

      getItem: jasmine.createSpy('getItem').and.callFake((key: string) =>
        sessionStorageMock.storage[key] ?? null
      ),

      setItem: jasmine.createSpy('setItem').and.callFake(
        (key: string, value: string) => {
          sessionStorageMock.storage[key] = value;
        }
      ),

      removeItem: jasmine.createSpy('removeItem').and.callFake(
        (key: string) => {
          delete sessionStorageMock.storage[key];
        }
      ),
    };

    Object.defineProperty(window, 'sessionStorage', {
      value: sessionStorageMock,
    });

    TestBed.configureTestingModule({
      providers: [AuthService, provideRouter([])],
    });

    service = TestBed.inject(AuthService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('login debe guardar token y generar usuario decodificado', () => {
    httpMock.post.and.returnValue(of({ token: TOKEN }));

    service.login('mail@test.com', '123').subscribe();

    expect(sessionStorageMock.setItem).toHaveBeenCalledWith(
      'jwt_token' as any,
      TOKEN as any
    );

    expect(service.currentUser()).toEqual(EXPECTED_USER);
  });

  it('logout debe limpiar token y usuario', () => {
    service.currentUser.set(EXPECTED_USER as CurrentUser);

    service.logout();
    expect(service.currentUser()).toBeNull();
  });

  it('getToken debe devolver token almacenado', () => {
    sessionStorageMock.storage['jwt_token'] = TOKEN;

    expect(service.getToken()).toBe(TOKEN);
  });

  it('isLoggedIn debe devolver true si hay usuario', () => {
    service.currentUser.set(EXPECTED_USER as CurrentUser);
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('constructor debe cargar usuario si ya hay token en sessionStorage', () => {
    sessionStorageMock.storage['jwt_token'] = TOKEN;

    service = TestBed.runInInjectionContext(() => new AuthService(httpMock));

    expect(service.currentUser()).toEqual(EXPECTED_USER);
  });

  it('hasRole should return true if user has one of the roles', () => {
    service.currentUser.set(EXPECTED_USER as CurrentUser);
    expect(service.hasRole([UserRole.ADMIN, UserRole.USER])).toBeTrue();
  });

  it('hasRole should return false if user does not have any of the roles', () => {
    service.currentUser.set(EXPECTED_USER as CurrentUser);
    expect(service.hasRole([UserRole.USER])).toBeFalse();
  });

  it('hasRole should return false if there is no user', () => {
    service.currentUser.set(null);
    expect(service.hasRole([UserRole.ADMIN])).toBeFalse();
  });
});
