import { Component, OnInit, Renderer2, RendererFactory2, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterModule, RouterOutlet } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';

import { AccountService } from 'app/core/auth/account.service';
import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { LoginService } from 'app/login/login.service';
import FooterComponent from '../footer/footer.component';
import NavbarComponent from '../navbar/navbar.component';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, RouterModule, FooterComponent, NavbarComponent],
})
export default class MainComponent implements OnInit {
  readonly account = inject(AccountService).trackCurrentAccount();
  readonly isAuthPage = signal(false);
  readonly todayLabel = new Intl.DateTimeFormat('fr-FR', {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(new Date());

  private readonly renderer: Renderer2;

  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly translateService = inject(TranslateService);
  private readonly rootRenderer = inject(RendererFactory2);

  constructor() {
    this.renderer = this.rootRenderer.createRenderer(document.querySelector('html'), null);
  }

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();
    this.updateShellState(this.router.url);

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateShellState(event.urlAfterRedirects);
      }
    });

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(document.querySelector('html'), 'lang', langChangeEvent.lang);
    });
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  private updateShellState(url: string): void {
    this.isAuthPage.set(
      url.startsWith('/login') ||
        url.startsWith('/account/register') ||
        url.startsWith('/account/reset') ||
        url.startsWith('/account/activate'),
    );
  }
}
