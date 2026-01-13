import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CategoryForm} from './category-form';
import {MatDialogRef} from '@angular/material/dialog';
import {CategoriesService} from '@app/core/services/categories.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {of, throwError} from 'rxjs';
import {Category} from '@app/core/models/categories/categorie.model';

describe('CategoryForm', () => {
  let component: CategoryForm;
  let fixture: ComponentFixture<CategoryForm>;
  let categoriesServiceMock: jasmine.SpyObj<CategoriesService>;
  let snackBarMock: jasmine.SpyObj<MatSnackBar>;
  let dialogRefMock: jasmine.SpyObj<MatDialogRef<CategoryForm>>;

  beforeEach(async () => {
    categoriesServiceMock = jasmine.createSpyObj('CategoriesService', ['getCategories', 'createCategory']);
    snackBarMock = jasmine.createSpyObj('MatSnackBar', ['open']);
    dialogRefMock = jasmine.createSpyObj('MatDialogRef', ['close']);

    categoriesServiceMock.getCategories.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [CategoryForm],
      providers: [
        {provide: MatDialogRef, useValue: dialogRefMock},
        {provide: CategoriesService, useValue: categoriesServiceMock},
        {provide: MatSnackBar, useValue: snackBarMock}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load categories on init', () => {
    const mockCategories: Category[] = [{id: 1, name: 'Test Category'} as Category];
    categoriesServiceMock.getCategories.and.returnValue(of(mockCategories));

    component.ngOnInit();

    expect(categoriesServiceMock.getCategories).toHaveBeenCalled();
    expect(component.categories).toEqual(mockCategories);
    expect(component.categoryForm.enabled).toBeTrue();
  });

  it('should handle error when loading categories', () => {
    categoriesServiceMock.getCategories.and.returnValue(throwError(() => new Error('Error')));

    component.ngOnInit();

    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Error al cargar las categorías disponibles',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-error']})
    );
  });

  it('should close dialog on cancel', () => {
    component.cancel();
    expect(dialogRefMock.close).toHaveBeenCalledWith(false);
  });

  it('should not submit if form is invalid', () => {
    component.categoryForm.setErrors({invalid: true});
    component.onSubmit();
    expect(categoriesServiceMock.createCategory).not.toHaveBeenCalled();
  });

  it('should create category and close dialog on success', () => {
    component.categoryForm.setValue({
      name: 'New Category',
      description: 'Description',
      parentCategoryId: null
    });
    categoriesServiceMock.createCategory.and.returnValue(of(1));

    component.onSubmit();

    expect(categoriesServiceMock.createCategory).toHaveBeenCalled();
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Categoría añadida correctamente',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-success']})
    );
    expect(dialogRefMock.close).toHaveBeenCalledWith(true);
  });

  it('should handle error when creating category', () => {
    component.categoryForm.setValue({
      name: 'New Category',
      description: 'Description',
      parentCategoryId: null
    });
    categoriesServiceMock.createCategory.and.returnValue(throwError(() => new Error('Error')));

    component.onSubmit();

    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Ha habido un error al añadir la nueva categoría',
      'Cerrar',
      jasmine.objectContaining({panelClass: ['snackbar-error']})
    );
    expect(dialogRefMock.close).toHaveBeenCalledWith(false);
  });
});
