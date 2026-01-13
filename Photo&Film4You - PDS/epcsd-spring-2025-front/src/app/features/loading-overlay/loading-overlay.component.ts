import {Component, computed} from '@angular/core';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {CommonModule} from '@angular/common';
import {LoadingService} from '@app/core/services/loading-service';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  template: `
    @if (isVisible()) {
      <div class="overlay">
        <mat-progress-spinner mode="indeterminate" diameter="60"></mat-progress-spinner>
      </div>
    }
  `,
  styles: [`
    .overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }
  `]
})
export class LoadingOverlayComponent {
  isVisible = computed(() => this.loadingService.isLoading());

  constructor(private readonly loadingService: LoadingService) {
  }
}
