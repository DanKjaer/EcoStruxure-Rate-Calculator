import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, Observable, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {SnackbarService} from '../services/snackbar.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private snackbarService: SnackbarService,
              private translateService: TranslateService,
              private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('EcoStructureProjectJWT');
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // If the user is unauthorized, redirect to the login page
          this.router.navigate(['/login']);
          this.snackbarService.openSnackBar(this.translateService.instant('ERROR_AUTHENTICATING'), false);
        }
        return throwError(() => error);
      })
    );
  }
}
