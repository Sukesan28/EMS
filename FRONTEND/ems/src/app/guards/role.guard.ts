import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const expectedRoles = route.data['roles'] as Array<string>;
  const userRole = authService.getRole();
  const user = authService.currentUser();
  const isFirstLogin = user?.firstLogin === true;

  if (localStorage.getItem('resettingUser') || isFirstLogin) {
    if (state.url === '/change-password') {
      return true;
    } else {
      router.navigate(['/change-password']);
      return false;
    }
  }

  if (userRole && expectedRoles && expectedRoles.includes(userRole)) {
    return true;
  }

  // Redirect to unauthorized if role doesn't match
  router.navigate(['/unauthorized']);
  return false;
};
