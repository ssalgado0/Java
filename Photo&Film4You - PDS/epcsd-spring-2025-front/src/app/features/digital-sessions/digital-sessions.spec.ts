import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DigitalSessions} from './digital-sessions';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {AuthService} from '@app/core/services/auth.service';
import {of, throwError} from 'rxjs';
import {DigitalSession} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';

describe('DigitalSessions', () => {
  let component: DigitalSessions;
  let fixture: ComponentFixture<DigitalSessions>;
  let digitalServiceSpy: jasmine.SpyObj<DigitalService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  const mockSessions: DigitalSession[] = [
    {id: 1, description: 'Session 1', email: 'test@test.com', status: DigitalStatus.AVAILABLE},
    {id: 2, description: 'Session 2', email: 'test@test.com', status: DigitalStatus.NOT_AVAILABLE}
  ];

  beforeEach(async () => {
    digitalServiceSpy = jasmine.createSpyObj('DigitalService', ['getUserDigitalSessions', 'getAllDigitalSessions']);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['currentUser']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [DigitalSessions, MatSnackBarModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: AuthService, useValue: authServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DigitalSessions);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should fetch user sessions on init', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of(mockSessions));
    fixture.detectChanges();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.getUserDigitalSessions).toHaveBeenCalled();
    expect(component.sessions).toEqual(mockSessions);
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should handle error when fetching sessions', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Ha ocurrido un error al cargar las sesiones digitales",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should toggle viewAll and fetch all sessions', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of([]));
    fixture.detectChanges();

    digitalServiceSpy.getAllDigitalSessions.and.returnValue(of(mockSessions));

    component.toggleViewAllSessions();

    expect(component.viewAll).toBeTrue();
    expect(digitalServiceSpy.getAllDigitalSessions).toHaveBeenCalled();
    expect(component.sessions).toEqual(mockSessions);
  });

  it('should toggle viewAll back to user sessions', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of([]));
    fixture.detectChanges();

    component.viewAll = true;

    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of(mockSessions));

    component.toggleViewAllSessions();

    expect(component.viewAll).toBeFalse();
    expect(digitalServiceSpy.getUserDigitalSessions).toHaveBeenCalledTimes(2); // Once on init, once on toggle
    expect(component.sessions).toEqual(mockSessions);
  });

  it('should return correct status class', () => {
    digitalServiceSpy.getUserDigitalSessions.and.returnValue(of([]));
    fixture.detectChanges();

    const availableSession = {status: DigitalStatus.AVAILABLE} as DigitalSession;
    const notAvailableSession = {status: DigitalStatus.NOT_AVAILABLE} as DigitalSession;
    const pendingSession = {status: DigitalStatus.REVIEW_PENDING} as DigitalSession;

    expect(component.getStatusClass(availableSession)).toBe('bg-success');
    expect(component.getStatusClass(notAvailableSession)).toBe('bg-danger');
    expect(component.getStatusClass(pendingSession)).toBe('bg-secondary');
  });
});
