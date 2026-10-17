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

export interface BlackjackState {
  id: number;
  player: string[];
  dealer: string[];
  playerTotal: number;
  dealerTotal: number | null;
  active: boolean;
  canDouble: boolean;
  result: string | null;
  bet: number;
  payout: number;
  balance: number;
  serverSeedHash: string;
  clientSeed: string;
  nonce: number;
}

export interface PokerState {
  id: number;
  hand: string[];
  active: boolean;
  result: string | null;
  multiplier: number;
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
  // ----- Blackjack -----
  blackjackCurrent() {
    return this.http.get<BlackjackState | null>(`${this.api}/games/blackjack/current`);
  }
  blackjackStart(amount: number) {
    return this.http.post<BlackjackState>(`${this.api}/games/blackjack/start`, { amount });
  }
  blackjackAction(action: 'hit' | 'stand' | 'double') {
    return this.http.post<BlackjackState>(`${this.api}/games/blackjack/${action}`, {});
  }

  // ----- Poker -----
  pokerCurrent() {
    return this.http.get<PokerState | null>(`${this.api}/games/poker/current`);
  }
  pokerDeal(amount: number) {
    return this.http.post<PokerState>(`${this.api}/games/poker/deal`, { amount });
  }
  pokerDraw(holds: boolean[]) {
    return this.http.post<PokerState>(`${this.api}/games/poker/draw`, { holds });
  }
}