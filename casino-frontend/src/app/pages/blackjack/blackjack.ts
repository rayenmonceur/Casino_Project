import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { BlackjackState, GameService } from '../../core/game';

@Component({
  selector: 'app-blackjack',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './blackjack.html',
  styleUrl: './blackjack.scss',
})
export class Blackjack {
  game = inject(GameService);

  amount = 10;
  chips = [5, 10, 50, 100];
  state = signal<BlackjackState | null>(null);
  busy = signal(false);
  error = signal('');

  constructor() {
    // Ken fama partie en cours (refresh de la page), on la reprend
    this.game.blackjackCurrent().subscribe({ next: (s) => s && this.state.set(s) });
  }

  start() { this.run(this.game.blackjackStart(this.amount)); }
  act(a: 'hit' | 'stand' | 'double') { this.run(this.game.blackjackAction(a)); }

  isRed(card: string) { return card.includes('♥') || card.includes('♦'); }

  private run(request: Observable<BlackjackState>) {
    this.error.set('');
    this.busy.set(true);
    request.subscribe({
      next: (s) => {
        this.state.set(s);
        this.game.balance.set(s.balance);
        this.busy.set(false);
      },
      error: (e) => {
        this.error.set(e.error?.error ?? 'Erreur, réessaie');
        this.busy.set(false);
      },
    });
  }
}