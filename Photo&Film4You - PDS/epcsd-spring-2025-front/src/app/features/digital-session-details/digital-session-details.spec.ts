import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DigitalSessionDetails} from './digital-session-details';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {ActivatedRoute, Router, UrlTree} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {DigitalService} from '@app/core/services/digital.service';
import {LoadingService} from '@app/core/services/loading-service';
import {of, throwError} from 'rxjs';
import {DigitalItem} from '@app/core/models/digital';
import {DigitalStatus} from '@app/core/enums/digital-status.enum';

describe('DigitalSessionDetails', () => {
  let component: DigitalSessionDetails;
  let fixture: ComponentFixture<DigitalSessionDetails>;
  let digitalServiceSpy: jasmine.SpyObj<DigitalService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockItems: DigitalItem[] = [
    {id: 1, digitalSessionId: 1, description: 'Item 1', lat: 0, lon: 0, link: 'http', status: DigitalStatus.AVAILABLE}
  ];

  beforeEach(async () => {
    digitalServiceSpy = jasmine.createSpyObj('DigitalService', ['getDigitalItemsBySessionId']);
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['show', 'hide']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    routerSpy = Object.assign(jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl']), {
      events: of(null)
    });
    routerSpy.createUrlTree.and.returnValue({} as UrlTree);
    routerSpy.serializeUrl.and.returnValue('');

    await TestBed.configureTestingModule({
      imports: [DigitalSessionDetails, MatSnackBarModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '1'
              }
            }
          }
        },
        {provide: Router, useValue: routerSpy},
        {provide: DigitalService, useValue: digitalServiceSpy},
        {provide: LoadingService, useValue: loadingServiceSpy},
        {provide: MatSnackBar, useValue: snackBarSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DigitalSessionDetails);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    digitalServiceSpy.getDigitalItemsBySessionId.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should fetch items on init', () => {
    digitalServiceSpy.getDigitalItemsBySessionId.and.returnValue(of(mockItems));
    fixture.detectChanges();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(digitalServiceSpy.getDigitalItemsBySessionId).toHaveBeenCalledWith(1);
    expect(component.items).toEqual(mockItems);
    expect(component.dataSource.data).toEqual(mockItems);
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
  });

  it('should handle error when fetching items', () => {
    digitalServiceSpy.getDigitalItemsBySessionId.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();

    expect(loadingServiceSpy.show).toHaveBeenCalled();
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      "Ha habido un error cargando los elementos de la sesión",
      "Cerrar",
      jasmine.any(Object)
    );
    expect(loadingServiceSpy.hide).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should return correct status class', () => {
    digitalServiceSpy.getDigitalItemsBySessionId.and.returnValue(of([]));
    fixture.detectChanges();

    const availableItem = {status: DigitalStatus.AVAILABLE} as DigitalItem;
    const notAvailableItem = {status: DigitalStatus.NOT_AVAILABLE} as DigitalItem;
    const pendingItem = {status: DigitalStatus.REVIEW_PENDING} as DigitalItem;

    expect(component.getStatusClass(availableItem)).toBe('bg-success');
    expect(component.getStatusClass(notAvailableItem)).toBe('bg-danger');
    expect(component.getStatusClass(pendingItem)).toBe('bg-secondary');
  });
});
