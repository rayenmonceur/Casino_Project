import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth';
import { Logo } from '../../shared/logo/logo';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, Logo],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  email = '';
  password = '';
  error = signal('');
  loading = signal(false);

  submit() {
    this.error.set('');
    this.loading.set(true);
    this.auth.register(this.username, this.email, this.password).subscribe({
      next: () => this.router.navigate(['/lobby']),
      error: (e) => {
        this.error.set(e.error?.error ?? 'Inscription impossible (vérifie les champs)');
        this.loading.set(false);
      },
    });
  }
}