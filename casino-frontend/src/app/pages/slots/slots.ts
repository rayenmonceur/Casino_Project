import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GameResult, GameService } from '../../core/game';

const ICONS: Record<string, string> = {
  CHERRY: '🍒',
  LEMON: '🍋',
  BELL: '🔔',
  STAR: '⭐',
  SEVEN: '7️⃣',
};
const ALL = Object.values(ICONS);

@Component({
  selector: 'app-slots',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './slots.html',
  styleUrl: './slots.scss',
})
export class Slots {
  game = inject(GameService);

  amount = 10;
  chips = [5, 10, 50, 100];
  reels = signal<string[]>(['🍒', '🍋', '🔔']);
  spinning = signal(false);
  result = signal<GameResult | null>(null);
  error = signal('');

  spin() {
    if (this.spinning()) return;
    this.error.set('');
    this.result.set(null);
    this.spinning.set(true);

    // Décor: les symboles défilent pendant que le serveur tire le résultat
    const timer = setInterval(() => this.reels.set([this.rand(), this.rand(), this.rand()]), 90);

    this.game.slots(this.amount).subscribe({
      next: (r) => {
        setTimeout(() => {
          clearInterval(timer);
          // Natija el 7a9i9a men el serveur
          this.reels.set(r.outcome.split(' | ').map((s) => ICONS[s]));
          this.result.set(r);
          this.game.balance.set(r.balance); // le solde change après l'animation
          this.spinning.set(false);
        }, 1600);
      },
      error: (e) => {
        clearInterval(timer);
        this.error.set(e.error?.error ?? 'Erreur, réessaie');
        this.spinning.set(false);
      },
    });
  }

  private rand() {
    return ALL[Math.floor(Math.random() * ALL.length)];
  }
}