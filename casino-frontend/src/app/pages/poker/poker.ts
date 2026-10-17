import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { GameService, PokerState } from '../../core/game';

@Component({
  selector: 'app-poker',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './poker.html',
  styleUrl: './poker.scss',
})
export class Poker {
  game = inject(GameService);

  amount = 10;
  chips = [5, 10, 50, 100];
  state = signal<PokerState | null>(null);
  holds = signal<boolean[]>([false, false, false, false, false]);
  busy = signal(false);
  error = signal('');

  paytable = [
    { id: 'ROYAL_FLUSH', label: 'Quinte flush royale', x: 800 },
    { id: 'STRAIGHT_FLUSH', label: 'Quinte flush', x: 50 },
    { id: 'FOUR_OF_A_KIND', label: 'Carré', x: 25 },
    { id: 'FULL_HOUSE', label: 'Full', x: 9 },
    { id: 'FLUSH', label: 'Couleur', x: 6 },
    { id: 'STRAIGHT', label: 'Quinte', x: 4 },
    { id: 'THREE_OF_A_KIND', label: 'Brelan', x: 3 },
    { id: 'TWO_PAIR', label: 'Double paire', x: 2 },
    { id: 'JACKS_OR_BETTER', label: 'Paire de Valets ou mieux', x: 1 },
  ];

  constructor() {
    this.game.pokerCurrent().subscribe({ next: (s) => s && this.state.set(s) });
  }

  deal() {
    this.holds.set([false, false, false, false, false]);
    this.run(this.game.pokerDeal(this.amount));
  }

  draw() {
    this.run(this.game.pokerDraw(this.holds()));
  }

  toggle(i: number) {
    if (!this.state()?.active) return;
    this.holds.update((h) => h.map((v, k) => (k === i ? !v : v)));
  }

  resultLabel(id: string | null) {
    return this.paytable.find((p) => p.id === id)?.label ?? 'Rien';
  }

  isRed(card: string) { return card.includes('♥') || card.includes('♦'); }

  private run(request: Observable<PokerState>) {
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