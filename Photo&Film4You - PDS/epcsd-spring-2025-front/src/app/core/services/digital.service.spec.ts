import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {DigitalService} from './digital.service';
import {AuthService} from '@app/core/services/auth.service';
import {environment} from '@app/environments/environment';
import {DigitalItem, DigitalSession} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';

describe('DigitalService', () => {
  let service: DigitalService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const fakeToken = 'fake-token';
  const baseUrl = environment.apiUrl;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken']);
    authServiceSpy.getToken.and.returnValue(fakeToken);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        DigitalService,
        {provide: AuthService, useValue: authServiceSpy}
      ]
    });
    service = TestBed.inject(DigitalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getUserDigitalSessions should GET from /digital', () => {
    const mockSessions: DigitalSession[] = [{
      id: 1,
      description: 'Test',
      email: 'test@test.com',
      status: DigitalStatus.AVAILABLE
    }];

    service.getUserDigitalSessions().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne(`${baseUrl}/digital`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockSessions);
  });

  it('getAllDigitalSessions should GET from /digital/allDigital', () => {
    const mockSessions: DigitalSession[] = [{
      id: 1,
      description: 'Test',
      email: 'test@test.com',
      status: DigitalStatus.AVAILABLE
    }];

    service.getAllDigitalSessions().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne(`${baseUrl}/digital/allDigital`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockSessions);
  });

  it('getDigitalSessionById should GET from /digital/:id', () => {
    const mockSession: DigitalSession = {
      id: 1,
      description: 'Test',
      email: 'test@test.com',
      status: DigitalStatus.AVAILABLE
    };
    const id = 1;

    service.getDigitalSessionById(id).subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`${baseUrl}/digital/${id}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockSession);
  });

  it('createDigitalSession should POST to /digital/createDigital', () => {
    const description = 'Test Session';
    const email = 'test@test.com';
    const mockId = 123;

    service.createDigitalSession(description, email).subscribe(id => {
      expect(id).toBe(mockId);
    });

    const req = httpMock.expectOne(`${baseUrl}/digital/createDigital`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({description, email});
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockId);
  });

  it('updateDigitalSession should PUT to /digital/updateDigital/:id', () => {
    const id = 1;
    const description = 'Updated Session';
    const email = 'updated@test.com';

    service.updateDigitalSession(id, description, email).subscribe(success => {
      expect(success).toBeTrue();
    });

    const req = httpMock.expectOne(`${baseUrl}/digital/updateDigital/${id}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({description, email});
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(true);
  });

  it('deleteDigitalSession should DELETE from /digital/removeDigital/:id', () => {
    const id = 1;

    service.deleteDigitalSession(id).subscribe(success => {
      expect(success).toBeTrue();
    });

    const req = httpMock.expectOne(`${baseUrl}/digital/removeDigital/${id}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(true);
  });

  it('countDigitalItemsBySessionId should GET from /digitalItem/digitalItemBySession with count param', () => {
    const id = 1;
    const mockCount = 5;

    service.countDigitalItemsBySessionId(id).subscribe(count => {
      expect(count).toBe(mockCount);
    });

    const req = httpMock.expectOne(req =>
      req.url === `${baseUrl}/digitalItem/digitalItemBySession` &&
      req.params.get('digitalSessionId') === id.toString() &&
      req.params.get('count') === 'true'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockCount);
  });

  it('getDigitalItemsBySessionId should GET from /digitalItem/digitalItemBySession', () => {
    const id = 1;
    const mockItems: DigitalItem[] = [{
      id: 1,
      digitalSessionId: 1,
      description: 'Item',
      lat: 0,
      lon: 0,
      link: 'http',
      status: DigitalStatus.AVAILABLE
    }];

    service.getDigitalItemsBySessionId(id).subscribe(items => {
      expect(items).toEqual(mockItems);
    });

    const req = httpMock.expectOne(req =>
      req.url === `${baseUrl}/digitalItem/digitalItemBySession` &&
      req.params.get('digitalSessionId') === id.toString()
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockItems);
  });

  it('getDigitalItemById should GET from /digitalItem/:id', () => {
    const id = 1;
    const mockItem: DigitalItem = {
      id: 1,
      digitalSessionId: 1,
      description: 'Item',
      lat: 0,
      lon: 0,
      link: 'http',
      status: DigitalStatus.AVAILABLE
    };

    service.getDigitalItemById(id).subscribe(item => {
      expect(item).toEqual(mockItem);
    });

    const req = httpMock.expectOne(`${baseUrl}/digitalItem/${id}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockItem);
  });

  it('addDigitalItem should POST to /digitalItem/addItem', () => {
    const item: DigitalItem = {
      digitalSessionId: 1,
      description: 'Item',
      lat: 0,
      lon: 0,
      link: 'http',
      status: DigitalStatus.AVAILABLE
    };
    const mockId = 123;

    service.addDigitalItem(item).subscribe(id => {
      expect(id).toBe(mockId);
    });

    const req = httpMock.expectOne(`${baseUrl}/digitalItem/addItem`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(item);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(mockId);
  });

  it('updateDigitalItem should PUT to /digitalItem/updateItem/:id', () => {
    const id = 1;
    const item: DigitalItem = {
      id: 1,
      digitalSessionId: 1,
      description: 'Item',
      lat: 0,
      lon: 0,
      link: 'http',
      status: DigitalStatus.AVAILABLE
    };

    service.updateDigitalItem(id, item).subscribe(success => {
      expect(success).toBeTrue();
    });

    const req = httpMock.expectOne(`${baseUrl}/digitalItem/updateItem/${id}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(item);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(true);
  });

  it('deleteDigitalItem should DELETE from /digitalItem/dropItem/:id', () => {
    const id = 1;

    service.deleteDigitalItem(id).subscribe(success => {
      expect(success).toBeTrue();
    });

    const req = httpMock.expectOne(`${baseUrl}/digitalItem/dropItem/${id}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${fakeToken}`);
    req.flush(true);
  });
});
