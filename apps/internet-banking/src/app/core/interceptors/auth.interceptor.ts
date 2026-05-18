import { HttpInterceptorFn } from '@angular/common/http';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.includes('/api/')) {
    return next(req);
  }

  return next(req.clone({
    withCredentials: true,
    setHeaders: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  }));
};
