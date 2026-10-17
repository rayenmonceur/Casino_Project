import { DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GameResult, GameService } from '../../core/game';

// Ordre réel des numéros sur une roulette européenne (sens horaire, 0 en haut)
const ORDER = [
  0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14,
  31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26,
];
const RED = new Set([1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36]);
const STEP = 360 / ORDER.length; // angle d'une case

const colorOf = (n: number) => (n === 0 ? '#1f8a5b' : RED.has(n) ? '#c0314a' : '#12172e');

@Component({
  selector: 'app-roulette',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './roulette.html',
  styleUrl: './roulette.scss',
})
export class Roulette {
  game = inject(GameService);

  types = [
    { id: 'RED', label: 'Rouge' },
    { id: 'BLACK', label: 'Noir' },
    { id: 'EVEN', label: 'Pair' },
    { id: 'ODD', label: 'Impair' },
    { id: 'NUMBER', label: 'Numéro' },
  ];
  chips = [10, 50, 100, 500];

  // Fond de la roue: une couleur par case
  gradient =
    'conic-gradient(' +
    ORDER.map((n, i) => `${colorOf(n)} ${i * STEP}deg ${(i + 1) * STEP}deg`).join(',') +
    ')';

  // Chaque numéro est placé au centre de sa case
  slots = ORDER.map((n, i) => ({
    n,
    transform: `translate(-50%, -50%) rotate(${(i + 0.5) * STEP}deg) translateY(-112px)`,
  }));

  betType = signal('RED');
  number = 7;
  amount = 50;

  rotation = signal(0);
  spinning = signal(false);
  result = signal<GameResult | null>(null);
  error = signal('');

  spin() {
    if (this.spinning()) return;
    this.error.set('');
    this.result.set(null);
    this.spinning.set(true);

    const num = this.betType() === 'NUMBER' ? this.number : undefined;
    this.game.roulette(this.betType(), this.amount, num).subscribe({
      next: (r) => {
        // Le serveur a déjà tiré le numéro: on fait simplement tomber la roue dessus
        const n = parseInt(r.outcome, 10); // "29 BLACK" -> 29
        const center = (ORDER.indexOf(n) + 0.5) * STEP;
        const base = Math.floor(this.rotation() / 360) * 360;
        this.rotation.set(base + 360 * 5 + (360 - center)); // 5 tours + arrêt sous la flèche

        setTimeout(() => {
          this.result.set(r);
          this.game.balance.set(r.balance); // le solde change après l'animation
          this.spinning.set(false);
        }, 4200);
      },
      error: (e) => {
        this.error.set(e.error?.error ?? 'Erreur, réessaie');
        this.spinning.set(false);
      },
    });
  }
}