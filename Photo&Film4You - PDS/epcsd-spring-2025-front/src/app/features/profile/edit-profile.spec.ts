import {TestBed} from '@angular/core/testing';
import {EditProfile} from './edit-profile';
import {UserService} from '@app/core/services/user.service';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import {UserProfile} from '@app/core/models/user/user-profile.model';

describe('EditProfile component (validaciones)', () => {
  let component: EditProfile;

  const userMock: UserProfile = {
    id: 1,
    email: 'test@test.com',
    fullName: 'Nombre Original',
    phoneNumber: '123456789',
  } as UserProfile;

  const userServiceMock = {
    getCurrentUser: jasmine.createSpy('getCurrentUser').and.returnValue(of(userMock)),
    updateCurrentUser: jasmine.createSpy('updateCurrentUser').and.returnValue(of(null)),
  };

  const routerMock = {
    navigate: jasmine.createSpy('navigate'),
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: () => null,
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditProfile],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: Router, useValue: routerMock },
        {provide: ActivatedRoute, useValue: activatedRouteMock},
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(EditProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('phoneNumberSoloNumeros', () => {
    component.form.setValue({
      fullName: 'Usuario Test',
      phoneNumber: 'abc123',
    });

    const phoneControl = component.form.get('phoneNumber');

    expect(component.form.invalid).toBeTrue();
    expect(phoneControl?.hasError('pattern')).toBeTrue();
  });
});
