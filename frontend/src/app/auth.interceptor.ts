import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const username = sessionStorage.getItem('authUser');
    const password = sessionStorage.getItem('authPass');

    if (username && password && req.url.startsWith('http://localhost:8080/api')) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: 'Basic ' + btoa(username + ':' + password)
        }
      });
      return next.handle(authReq);
    }

    return next.handle(req);
  }
}
