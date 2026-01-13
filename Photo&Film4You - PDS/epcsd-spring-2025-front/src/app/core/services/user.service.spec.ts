import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { UserService, CreateUserRequest } from './user.service';
import { UserRole } from '@app/core/enums/user-role.enum';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('createUser should POST /users and return id', () => {
    const payload: CreateUserRequest = {
      fullName: 'Test User',
      email: 'test@example.com',
      password: 'Password123',
      phoneNumber: '600600600',
      role: UserRole.USER
    };

    let result: number | undefined;

    service.createUser(payload).subscribe((id) => (result = id));

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/users'));
    expect(req.request.body).toEqual(payload);

    req.flush(99);
    expect(result).toBe(99);
  });
});
