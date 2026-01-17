import { Component, NgZone, OnInit, computed, inject, signal } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { IClient } from '../client.model';

import { ClientService, EntityArrayResponseType } from '../service/client.service';
import { ClientDeleteDialogComponent } from '../delete/client-delete-dialog.component';

@Component({
  selector: 'jhi-client',
  templateUrl: './client.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FilterComponent, ItemCountComponent],
})
export class ClientComponent implements OnInit {
  subscription: Subscription | null = null;
  clients = signal<IClient[]>([]);
  isLoading = false;
  addressSearch = signal('');
  userSearch = signal('');
  filteredClients = computed(() => {
    const addressTerm = this.addressSearch().trim().toLowerCase();
    const userTerm = this.userSearch().trim().toLowerCase();

    return this.clients().filter(client => {
      const address = (client.adresse ?? '').toLowerCase();
      const userLabel = (this.displayUser(client.user) ?? '').toLowerCase();

      const addressMatch = !addressTerm || address.includes(addressTerm);
      const userMatch = !userTerm || userLabel.includes(userTerm);
      return addressMatch && userMatch;
    });
  });
  clientsWithGeo = computed(() => this.clients().filter(client => this.hasCoordinates(client)).length);
  geoCoverageRatio = computed(() => {
    const total = this.clients().length;
    if (!total) {
      return 0;
    }

    return Math.round((this.clientsWithGeo() / total) * 100);
  });
  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly clientService = inject(ClientService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IClient): number => this.clientService.getClientIdentifier(item);

  onAddressSearchChange(term: string): void {
    this.addressSearch.set(term);
  }

  onUserSearchChange(term: string): void {
    this.userSearch.set(term);
  }

  clearAddressSearch(): void {
    this.addressSearch.set('');
  }

  clearUserSearch(): void {
    this.userSearch.set('');
  }

  hasCoordinates(client: IClient | null | undefined): boolean {
    return client?.latitude !== null && client?.latitude !== undefined && client?.longitude !== null && client?.longitude !== undefined;
  }

  displayUser(user: IClient['user'] | null | undefined): string | null {
    if (!user) {
      return null;
    }

    const name = [user.firstName, user.lastName]
      .filter(part => !!part && part.trim().length > 0)
      .join(' ')
      .trim();

    if (name.length > 0) {
      return name;
    }

    return user.login ?? null;
  }

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  delete(client: IClient): void {
    const modalRef = this.modalService.open(ClientDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.client = client;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.clients.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IClient[] | null): IClient[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page, filters } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.clientService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
