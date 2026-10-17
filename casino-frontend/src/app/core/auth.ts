import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private api = environment.apiUrl;

  // El user mconnecté (yetsajjel fel navigateur bech ma yetfasakhch ki ndawwrou el page)
  private _user = signal<AuthResponse | null>(this.load());
  user = this._user.asReadonly();
  isLoggedIn = computed(() => !!this._user());
  isAdmin = computed(() => this._user()?.role === 'ADMIN');

  token(): string | null {
    return this._user()?.token ?? null;
  }

  login(username: string, password: string) {
    return this.http
      .post<AuthResponse>(`${this.api}/auth/login`, { username, password })
      .pipe(tap((res) => this.save(res)));
  }

  register(username: string, email: string, password: string) {
    return this.http
      .post<AuthResponse>(`${this.api}/auth/register`, { username, email, password })
      .pipe(tap((res) => this.save(res)));
  }

  logout() {
    localStorage.removeItem('qartaj_user');
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  private save(res: AuthResponse) {
    localStorage.setItem('qartaj_user', JSON.stringify(res));
    this._user.set(res);
  }

  private load(): AuthResponse | null {
    try {
      return JSON.parse(localStorage.getItem('qartaj_user') ?? 'null');
    } catch {
      return null;
    }
  }
}