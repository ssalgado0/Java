import {Component, computed, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CategoriesService} from '@app/core/services/categories.service';
import {ProductService} from '@app/core/services/product.service'
import {Category} from '@app/core/models/categories/categorie.model'
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatError, MatFormField, MatLabel, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {CategoryForm} from '@app/features/category-form/category-form';
import {AuthService} from '@app/core/services/auth.service';
import {MatTooltip} from '@angular/material/tooltip';
import {map, of, switchMap, tap} from 'rxjs';


@Component({
  selector: 'create-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule, MatFormField, MatLabel, MatInput, MatError, MatIcon, MatSelect, MatOption, MatButton, MatIconButton, MatSuffix, MatTooltip, RouterLink],
  templateUrl: './create-product.html'
})
export class CreateProduct implements OnInit {

  currentUser = computed(() => this.auth.currentUser());

  productForm!: FormGroup;
  categories: Category[] = [];

  isEditMode = false;
  productId: number | null = null;

  constructor(
    private readonly fb: FormBuilder,
    private readonly categoriesService: CategoriesService,
    private readonly productService: ProductService,
    private readonly auth: AuthService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialog: MatDialog,
  ) {
    this.productForm = this.fb.group({
      nombre: ['', [Validators.required]],
      descripcion: ['', [Validators.required]],
      precioDiario: [null, [Validators.required, Validators.min(0)]],
      marca: ['', [Validators.required]],
      modelo: ['', [Validators.required]],
      categoria: [null, [Validators.required]],
    });
  }

  ngOnInit() {

    this.fetchCategories();

    this.productForm.disable();

    this.route.paramMap
    .pipe(
      tap(() => this.productForm.disable()),
      switchMap(params => {
        const id = params.get('id');
        this.productId = id ? Number.parseInt(id) : null;
        this.isEditMode = !!this.productId;

        if (!this.isEditMode || !this.productId) {
          this.productForm.enable();
          return of(null);
        }

        return this.productService.getProductById(this.productId);
      })
    )
    .subscribe({
      next: product => {
        if (product) {
          this.productForm.patchValue({
            nombre: product.name,
            descripcion: product.description,
            precioDiario: product.dailyPrice,
            marca: product.brand,
            modelo: product.model,
            categoria: product.categoryId
          });
        }
        this.productForm.enable();
      },
      error: () => {
        this.productForm.enable();
        this.snackBar.open("Ha habido un error al cargar el producto.", "Cerrar", {
          duration: 5000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }


  onSubmit() {
    if (this.productForm.valid) {

      const product = {
        name: this.productForm.value.nombre,
        description: this.productForm.value.descripcion,
        dailyPrice: this.productForm.value.precioDiario,
        model: this.productForm.value.modelo,
        brand: this.productForm.value.marca,
        categoryId: this.productForm.value.categoria,
      };


      const observable = this.isEditMode && this.productId
        ? this.productService.updateProduct(this.productId, product).pipe(
          map(ok => {
            if (!ok) throw new Error('Update returned false');
            return this.productId!;
          })
        )
        : this.productService.createProduct(product);


      observable.subscribe({
        next: () => {
          this.router.navigate(['/products']);
          this.snackBar.open(`Producto ${this.isEditMode ? 'actualizado' : 'creado'} correctamente`, 'Cerrar', {
            duration: 3000,
            panelClass: ['snackbar-success']
          });
        },
        error: () => {
          this.snackBar.open(`Error al ${this.isEditMode ? 'actualizar' : 'crear'} el producto.`, 'Cerrar', {
            duration: 3000,
            panelClass: ['snackbar-error']
          });
        }
      });
    } else {
      this.snackBar.open(`Error al ${this.isEditMode ? 'actualizar' : 'crear'} el producto.`, 'Cerrar', {
        duration: 3000,
        panelClass: ['snackbar-error']
      });
      this.productForm.markAllAsTouched();
    }
  }

  fetchCategories(): void {
    this.productForm.disable();

    this.categoriesService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.productForm.enable();
      },
      error: () => {
        this.snackBar.open('Error al cargar las categorías disponibles', 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  openCreateCategoryDialog(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();

    this.dialog.open(CategoryForm, {
      width: '480px'
    })
      .afterClosed()
      .subscribe((isCreated: boolean) => {
        if (isCreated) {
          this.fetchCategories();
        }
      });
  }
}
