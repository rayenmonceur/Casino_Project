import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface GameResult {
  game: string;
  outcome: string;
  win: boolean;
  bet: number;
  payout: number;
  balance: number;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
}

@Injectable({ providedIn: 'root' })
export class GameService {
  private http = inject(HttpClient);
  private api = environment.apiUrl;

  // Le solde s'affiche dans le header et le lobby
  balance = signal<number | null>(null);

  refreshBalance() {
    this.http
      .get<{ balance: number }>(`${this.api}/wallet/balance`)
      .subscribe({ next: (r) => this.balance.set(r.balance) });
  }

  // Les composants mettent à jour le solde après l'animation
  roulette(betType: string, amount: number, number?: number) {
    return this.http.post<GameResult>(`${this.api}/games/roulette`, { betType, amount, number });
  }

  slots(amount: number) {
    return this.http.post<GameResult>(`${this.api}/games/slots`, { amount });
  }
  dice(direction: 'UNDER' | 'OVER', target: number, amount: number) {
  return this.http.post<GameResult>(`${this.api}/games/dice`, { direction, target, amount });
}
}