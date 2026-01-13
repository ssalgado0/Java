import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Navbar} from '@app/features/navbar/navbar';
import {LoadingOverlayComponent} from '@app/features/loading-overlay/loading-overlay.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    Navbar,
    LoadingOverlayComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
