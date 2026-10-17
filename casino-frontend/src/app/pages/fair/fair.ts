import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FairClient, FairInfo, Reveal, Verified } from '../../core/fair';

@Component({
  selector: 'app-fair',
  imports: [FormsModule, RouterLink],
  templateUrl: './fair.html',
  styleUrl: './fair.scss',
})
export class Fair {
  private api = inject(FairClient);

  info = signal<FairInfo | null>(null);
  reveal = signal<Reveal | null>(null);
  verified = signal<Verified | null>(null);
  error = signal('');

  newClientSeed = '';

  // Formulaire de vérification
  vServer = '';
  vHash = '';
  vClient = '';
  vNonce = 0;
  vGame = 'ROULETTE';

  constructor() {
    this.refresh();
  }

  refresh() {
    this.api.current().subscribe({ next: (i) => { this.info.set(i); this.newClientSeed = i.clientSeed; } });
  }

  saveClientSeed() {
    this.error.set('');
    this.api.setClientSeed(this.newClientSeed).subscribe({
      next: (i) => this.info.set(i),
      error: (e) => this.error.set(e.error?.error ?? 'Erreur'),
    });
  }

  rotate() {
    this.api.rotate().subscribe({
      next: (r) => {
        this.reveal.set(r);
        // Nعمروا formulaire el vérification wa7dou
        this.vServer = r.serverSeed;
        this.vHash = r.serverSeedHash;
        this.vClient = r.clientSeed;
        this.vNonce = 0;
        this.refresh();
      },
      error: (e) => this.error.set(e.error?.error ?? 'Erreur'),
    });
  }

  verify() {
    this.error.set('');
    const bounds =
  this.vGame === 'ROULETTE' ? [37] : this.vGame === 'SLOTS' ? [5, 5, 5] : [10000];
    this.api
      .verify({ serverSeed: this.vServer, serverSeedHash: this.vHash, clientSeed: this.vClient, nonce: this.vNonce, bounds })
      .subscribe({
        next: (v) => this.verified.set(v),
        error: (e) => this.error.set(e.error?.error ?? 'Vérification impossible'),
      });
  }
}