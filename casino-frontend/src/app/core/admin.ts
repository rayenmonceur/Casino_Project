import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Tx {
  id: number;
  sender: string;    // émetteur
  receiver: string;  // récepteur
  amount: number;
  type: string;      // BET, WIN, BONUS
  game: string;
  createdAt: string;
}

export interface SecEvent {
  id: number;
  type: string;      // LOGIN_FAILED, LOGIN_BLOCKED, RATE_LIMIT
  ip: string;
  username: string | null;
  details: string;
  createdAt: string;
}

export interface Stats {
  users: number;
  totalBets: number;
  totalWins: number;
  casinoProfit: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/admin`;

  stats() { return this.http.get<Stats>(`${this.api}/stats`); }
  transactions() { return this.http.get<Tx[]>(`${this.api}/transactions`); }
  events() { return this.http.get<SecEvent[]>(`${this.api}/security-events`); }
}