import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isResetting = localStorage.getItem('resettingUser') !== null;
  const user = authService.currentUser();
  const isFirstLogin = user?.firstLogin === true;

  if (authService.isAuthenticated()) {
    if ((isResetting || isFirstLogin) && state.url !== '/change-password') {
      router.navigate(['/change-password']);
      return false;
    }
    return true;
  }

  if (isResetting) {
    if (state.url === '/change-password') {
      return true;
    } else {
      router.navigate(['/change-password']);
      return false;
    }
  }

  // Redirect to login page if not authenticated
  router.navigate(['/login']);
  return false;
};
