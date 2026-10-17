import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GameResult, GameService } from '../../core/game';

@Component({
  selector: 'app-dice',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './dice.html',
  styleUrl: './dice.scss',
})
export class Dice {
  game = inject(GameService);

  direction: 'UNDER' | 'OVER' = 'UNDER';
  target = 50;
  amount = 10;
  chips = [5, 10, 50, 100];

  display = signal('--.--');
  rollPos = signal<number | null>(null);
  rolling = signal(false);
  result = signal<GameResult | null>(null);
  error = signal('');

  // Chance de gagner en % (UNDER 50 = 50%, OVER 50 = 50%)
  get chance() {
    return this.direction === 'UNDER' ? this.target : 100 - this.target;
  }

  // Même formule que le serveur: 97 / chance
  get multiplier() {
    return Math.floor((9700 / this.chance)) / 100;
  }

  get potentialWin() {
    return Math.floor(this.amount * 97 / this.chance * 100) / 100;
  }

  // Barre: zone verte = zone gagnante
  get barBg() {
    const t = this.target;
    return this.direction === 'UNDER'
      ? `linear-gradient(90deg, #2ec4b6 ${t}%, #c0314a ${t}%)`
      : `linear-gradient(90deg, #c0314a ${t}%, #2ec4b6 ${t}%)`;
  }

  roll() {
    if (this.rolling()) return;
    this.error.set('');
    this.result.set(null);
    this.rollPos.set(null);
    this.rolling.set(true);

    // Décor: chiffres aléatoires pendant que le serveur tire le vrai résultat
    const timer = setInterval(
      () => this.display.set((Math.random() * 100).toFixed(2)),
      60,
    );

    this.game.dice(this.direction, this.target, this.amount).subscribe({
      next: (r) => {
        setTimeout(() => {
          clearInterval(timer);
          const value = r.outcome.split(' ')[0]; // "47.32 UNDER 50" -> "47.32"
          this.display.set(value);
          this.rollPos.set(parseFloat(value));
          this.result.set(r);
          this.game.balance.set(r.balance); // le solde change après l'animation
          this.rolling.set(false);
        }, 1200);
      },
      error: (e) => {
        clearInterval(timer);
        this.error.set(e.error?.error ?? 'Erreur, réessaie');
        this.rolling.set(false);
      },
    });
  }
}