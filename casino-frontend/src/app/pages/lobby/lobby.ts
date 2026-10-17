import { DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth';
import { GameService } from '../../core/game';

@Component({
  selector: 'app-lobby',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './lobby.html',
  styleUrl: './lobby.scss',
})
export class Lobby {
  auth = inject(AuthService);
  game = inject(GameService);
}