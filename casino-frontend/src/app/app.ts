import { DecimalPipe } from '@angular/common';
import { Component, effect, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth';
import { GameService } from './core/game';
import { Logo } from './shared/logo/logo';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, DecimalPipe, Logo],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  auth = inject(AuthService);
  game = inject(GameService);

  constructor() {
    // Dès que l'utilisateur est connecté, on charge son solde
    effect(() => {
      if (this.auth.isLoggedIn()) this.game.refreshBalance();
    });
  }
}