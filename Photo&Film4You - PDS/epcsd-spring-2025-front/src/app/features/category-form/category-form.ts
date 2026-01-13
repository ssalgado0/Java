import {Component, inject, OnInit} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {CategoriesService} from '@app/core/services/categories.service';
import {Category} from '@app/core/models/categories/categorie.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [
    MatDialogTitle,
    ReactiveFormsModule,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatError,
    MatOption,
    MatSelect
  ],
  templateUrl: './category-form.html',
  styleUrl: './category-form.css',
})
export class CategoryForm implements OnInit {
  categories: Category[] = [];
  private readonly fb = inject(FormBuilder);
  categoryForm: FormGroup = this.fb.group({
    name: ['', [Validators.required]],
    description: [''],
    parentCategoryId: ['']
  });
  private readonly dialogRef = inject(MatDialogRef<CategoryForm>);
  private readonly categoriesService = inject(CategoriesService);
  private readonly snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.categoryForm.disable();
    this.categoriesService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.categoryForm.enable();
      },
      error: () => {
        this.snackBar.open('Error al cargar las categorías disponibles', 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      return;
    }

    const category: Category = this.categoryForm.value;

    this.categoriesService.createCategory(category).subscribe({
      next: () => {
        this.snackBar.open(`Categoría añadida correctamente`, 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Ha habido un error al añadir la nueva categoría', 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
        this.dialogRef.close(false);
      }
    });
  }
}
