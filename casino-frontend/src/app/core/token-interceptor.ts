import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  // Ken el token fet (401) nkharrjou el user
  return next(req).pipe(
    catchError((err) => {
      if (err.status === 401 && token) auth.logout();
      return throwError(() => err);
    }),
  );
};