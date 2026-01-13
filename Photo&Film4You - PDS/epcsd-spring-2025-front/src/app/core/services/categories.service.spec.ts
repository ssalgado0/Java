import {TestBed} from '@angular/core/testing';
import {CategoriesService} from './categories.service';
import {HttpClient} from '@angular/common/http';
import {of} from 'rxjs';
import {Category} from '../models/categories/categorie.model';
import {environment} from '@app/environments/environment';
import {AuthService} from '@app/core/services/auth.service';

describe('CategoriesService', () => {
  let service: CategoriesService;
  let httpMock: jasmine.SpyObj<HttpClient>;
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    httpMock = jasmine.createSpyObj('HttpClient', ['get', 'post']);
    authServiceMock = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.overrideProvider(HttpClient, { useValue: httpMock });
    TestBed.overrideProvider(AuthService, {useValue: authServiceMock});

    TestBed.configureTestingModule({
      providers: [
        CategoriesService,
        {provide: AuthService, useValue: authServiceMock}
      ]
    });

    service = TestBed.inject(CategoriesService);
  });

  it('debería llamar a HttpClient.get con la URL correcta', () => {
    const mockCategories: Category[] = [{ id: 1, name: 'Test' } as Category];

    httpMock.get.and.returnValue(of(mockCategories));

    service.getCategories().subscribe();

    const expectedUrl = `${environment.apiUrl}/categories`;

    expect(httpMock.get).toHaveBeenCalledWith(expectedUrl);
  });

  it('debería actualizar el signal categories al recibir datos', () => {
    const mockCategories: Category[] = [
      { id: 1, name: 'Coches' } as Category,
      { id: 2, name: 'Herramientas' } as Category
    ];

    httpMock.get.and.returnValue(of(mockCategories));

    service.getCategories().subscribe();

    expect(service.categories()).toEqual(mockCategories);
  });

  it('debería devolver las categorías recibidas del backend', (done) => {
    const mockCategories: Category[] = [{ id: 1, name: 'Test' } as Category];

    httpMock.get.and.returnValue(of(mockCategories));

    service.getCategories().subscribe((result) => {
      expect(result).toEqual(mockCategories);
      done();
    });
  });

  it('should create category with correct URL and headers', () => {
    const mockCategory: Category = {id: 1, name: 'New Category'} as Category;
    const mockToken = 'mock-token';
    authServiceMock.getToken.and.returnValue(mockToken);
    httpMock.post.and.returnValue(of(1));

    service.createCategory(mockCategory).subscribe();

    const expectedUrl = `${environment.apiUrl}/categories`;
    expect(httpMock.post).toHaveBeenCalledWith(expectedUrl, mockCategory, {
      headers: jasmine.objectContaining({
        Authorization: `Bearer ${mockToken}`
      })
    });
  });

  it('should create category with empty token if not available', () => {
    const mockCategory: Category = {id: 1, name: 'New Category'} as Category;
    authServiceMock.getToken.and.returnValue(null);
    httpMock.post.and.returnValue(of(1));

    service.createCategory(mockCategory).subscribe();

    const expectedUrl = `${environment.apiUrl}/categories`;
    expect(httpMock.post).toHaveBeenCalledWith(expectedUrl, mockCategory, {
      headers: jasmine.objectContaining({
        Authorization: `Bearer `
      })
    });
  });
});
