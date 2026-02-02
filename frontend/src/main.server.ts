import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideServerRendering } from '@angular/platform-server';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

const bootstrap = () =>
  bootstrapApplication(AppComponent, {
    providers: [
      provideServerRendering(),
      importProvidersFrom(HttpClientModule)
    ]
  });

export default bootstrap;
