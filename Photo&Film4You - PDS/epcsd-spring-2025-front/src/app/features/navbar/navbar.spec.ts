import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Navbar } from './navbar';
import { AuthService } from '@app/core/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, ActivatedRoute, UrlTree } from '@angular/router';
import { Login } from '@app/features/login/login';
import { of } from 'rxjs';
import { CartService } from '@app/core/services/cart.service';
import { UserRole } from '@app/core/enums/user-role.enum';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('Navbar', () => {
  let component: Navbar;
  let fixture: ComponentFixture<Navbar>;
  let authMock: jasmine.SpyObj<AuthService>;
  let dialogMock: jasmine.SpyObj<MatDialog>;
  let routerMock: jasmine.SpyObj<Router>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let cartMock: jasmine.SpyObj<CartService>;

  beforeEach(async () => {
    authMock = jasmine.createSpyObj('AuthService', ['currentUser', 'logout', 'getToken']);
    authMock.getToken.and.returnValue('fake-token');
    dialogMock = jasmine.createSpyObj('MatDialog', ['open']);
    cartMock = jasmine.createSpyObj('CartService', ['clear'], { cart$: of({ countItems: () => 0 }) });

    routerMock = Object.assign(
      jasmine.createSpyObj<Router>('Router', [
        'navigateByUrl',
        'createUrlTree',
        'serializeUrl'
      ]),
      {
        events: of(),
      }
    );
    routerMock.navigateByUrl.and.returnValue(Promise.resolve(true));
    routerMock.createUrlTree.and.returnValue({} as unknown as UrlTree);
    routerMock.serializeUrl.and.returnValue('/');
    snackBarMock = jasmine.createSpyObj('MatSnackBar', ['open']);

    authMock.currentUser.and.returnValue(null);

    await TestBed.configureTestingModule({
      imports: [Navbar, HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authMock },
        { provide: MatDialog, useValue: dialogMock },
        { provide: CartService, useValue: cartMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: ActivatedRoute, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Navbar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debería abrir el diálogo de login con las opciones correctas', () => {
    component.openLoginDialog();
    expect(dialogMock.open).toHaveBeenCalledWith(Login, jasmine.objectContaining({
      width: '90vw',
      maxWidth: '400px',
      maxHeight: '90vh'
    }));
  });

  it('debería hacer logout, limpiar carrito, navegar y mostrar snackbar', async () => {
    component.logout();

    expect(cartMock.clear).toHaveBeenCalled();
    expect(authMock.logout).toHaveBeenCalled();
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith('/');

    await Promise.resolve();
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Sesión cerrada correctamente',
      'cerrar',
      jasmine.objectContaining({ duration: 3000 })
    );
  });

  it('debería devolver avatar por defecto si no hay nombre', () => {
    const url = component.avatarUrl();
    expect(url).toContain('https://ui-avatars.com/api/');
    expect(url).toContain('background=005cbb');
  });

  it('debería devolver avatar con nombre codificado', () => {
    const url = component.avatarUrl('Javier Yugsi');
    expect(url).toContain(encodeURIComponent('Javier Yugsi'));
    expect(url).toContain('bold=true');
  });

  it('debería mostrar "Alta usuario" solo si es admin', () => {
    authMock.currentUser.and.returnValue({ fullName: 'Admin', role: UserRole.ADMIN } as any);
    fixture = TestBed.createComponent(Navbar);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    const alta = Array.from(el.querySelectorAll('a')).find(a => a.textContent?.includes('Alta usuario'));
    expect(alta).toBeTruthy();
  });

  it('NO debería mostrar "Alta usuario" si no es admin', () => {
    authMock.currentUser.and.returnValue({ fullName: 'User', role: UserRole.USER } as any);
    fixture = TestBed.createComponent(Navbar);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    const alta = Array.from(el.querySelectorAll('a')).find(a => a.textContent?.includes('Alta usuario'));
    expect(alta).toBeFalsy();
  });
});
