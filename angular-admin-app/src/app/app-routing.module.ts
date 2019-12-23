/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home/home.component';

import { ParcelComponent } from './Parcel/Parcel.component';
import { UnitComponent } from './Unit/Unit.component';

import { CooperativeComponent } from './Cooperative/Cooperative.component';
import { TransportCompComponent } from './TransportComp/TransportComp.component';
import { SellerComponent } from './Seller/Seller.component';
import { GuestComponent } from './Guest/Guest.component';

import { AddUnitToParcelComponent } from './AddUnitToParcel/AddUnitToParcel.component';
import { TradeComponent } from './Trade/Trade.component';
import { PutParcelIntoStockComponent } from './PutParcelIntoStock/PutParcelIntoStock.component';
import { ForSaleComponent } from './ForSale/ForSale.component';
import { SoldComponent } from './Sold/Sold.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'Parcel', component: ParcelComponent },
  { path: 'Unit', component: UnitComponent },
  { path: 'Cooperative', component: CooperativeComponent },
  { path: 'TransportComp', component: TransportCompComponent },
  { path: 'Seller', component: SellerComponent },
  { path: 'Guest', component: GuestComponent },
  { path: 'AddUnitToParcel', component: AddUnitToParcelComponent },
  { path: 'Trade', component: TradeComponent },
  { path: 'PutParcelIntoStock', component: PutParcelIntoStockComponent },
  { path: 'ForSale', component: ForSaleComponent },
  { path: 'Sold', component: SoldComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
 imports: [RouterModule.forRoot(routes)],
 exports: [RouterModule],
 providers: []
})
export class AppRoutingModule { }
