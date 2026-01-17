import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import SharedModule from 'app/shared/shared.module';
import { Authority } from 'app/config/authority.constants';
import { LANGUAGES } from 'app/config/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { environment } from 'environments/environment';

type SidebarItem = {
  label: string;
  route: string;
  icon: string;
};

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  imports: [RouterModule, SharedModule],
})
export default class NavbarComponent implements OnInit {
  inProduction?: boolean;
  isSidebarOpen = signal(false);
  languages = LANGUAGES;
  openAPIEnabled?: boolean;
  version = '';
  account = inject(AccountService).trackCurrentAccount();
  readonly authority = Authority;
  readonly businessMenuItems: SidebarItem[] = [
    { label: 'Clients', route: '/client', icon: 'users' },
    { label: 'Techniciens', route: '/technicien', icon: 'user' },
    { label: 'Missions', route: '/mission', icon: 'road' },
    { label: 'Paiements', route: '/paiement', icon: 'list' },
    { label: 'Documents', route: '/document', icon: 'book' },
    { label: 'Services', route: '/service-jef', icon: 'cogs' },
  ];

  readonly adminMenuItems: SidebarItem[] = [
    { label: 'Authority', route: '/authority', icon: 'asterisk' },
    { label: 'Utilisateurs', route: '/admin/user-management', icon: 'users' },
    { label: 'Métriques', route: '/admin/metrics', icon: 'tachometer-alt' },
    { label: 'Diagnostics', route: '/admin/health', icon: 'heart' },
    { label: 'Configuration', route: '/admin/configuration', icon: 'cogs' },
    { label: 'Logs', route: '/admin/logs', icon: 'tasks' },
  ];

  private readonly loginService = inject(LoginService);
  private readonly translateService = inject(TranslateService);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  constructor() {
    const { VERSION } = environment;
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    }
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.openAPIEnabled = profileInfo.openAPIEnabled;
    });
  }

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }

  closeSidebar(): void {
    this.isSidebarOpen.set(false);
  }

  login(): void {
    this.closeSidebar();
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.closeSidebar();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  toggleSidebar(): void {
    this.isSidebarOpen.update(isSidebarOpen => !isSidebarOpen);
  }

  hasAuthority(role: Authority): boolean {
    return this.account()?.authorities?.includes(role) ?? false;
  }
}
