import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/auth-guard';
import { Login } from './pages/login/login';
import { Lobby } from './pages/lobby/lobby';
import { Register } from './pages/register/register';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'lobby', component: Lobby, canActivate: [authGuard] },
  {
    path: 'roulette',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/roulette/roulette').then((m) => m.Roulette),
  },
  {
    path: 'slots',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/slots/slots').then((m) => m.Slots),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./pages/admin/admin').then((m) => m.Admin),
  },
  {
  path: 'fair',
  canActivate: [authGuard],
  loadComponent: () => import('./pages/fair/fair').then((m) => m.Fair),
},
{
  path: 'dice',
  canActivate: [authGuard],
  loadComponent: () => import('./pages/dice/dice').then((m) => m.Dice),
},
{
  path: 'blackjack',
  canActivate: [authGuard],
  loadComponent: () => import('./pages/blackjack/blackjack').then((m) => m.Blackjack),
},
{
  path: 'poker',
  canActivate: [authGuard],
  loadComponent: () => import('./pages/poker/poker').then((m) => m.Poker),
},
  { path: '', pathMatch: 'full', redirectTo: 'lobby' },
  { path: '**', redirectTo: 'lobby' },
];