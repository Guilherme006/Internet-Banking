import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { LayoutComponent } from './app/presentation/features/layout/layout.component';

bootstrapApplication(LayoutComponent, appConfig)
  .catch(err => console.error('Falha ao inicializar aplicação:', err));
