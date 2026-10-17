import { Component, input } from '@angular/core';

// Logo QARTAJ: symbole de Tanit (cercle + barre + triangle)
@Component({
  selector: 'app-logo',
  template: `
    <svg
      [attr.width]="size()"
      [attr.height]="size()"
      viewBox="0 0 64 64"
      fill="none"
      stroke="currentColor"
      stroke-width="3"
      stroke-linecap="round"
      stroke-linejoin="round"
      aria-label="Logo Qartaj"
    >
      <circle cx="32" cy="9" r="5" />
      <path d="M10 27 L12 20 M54 27 L52 20" />
      <path d="M10 27 H54" />
      <path d="M32 15 L15 58 H49 Z" />
    </svg>
  `,
  styles: [':host { display: inline-flex; color: var(--gold); }'],
})
export class Logo {
  size = input(64);
}