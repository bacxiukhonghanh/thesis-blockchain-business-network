import {Asset} from './org.hyperledger.composer.system';
import {Participant} from './org.hyperledger.composer.system';
import {Transaction} from './org.hyperledger.composer.system';
import {Event} from './org.hyperledger.composer.system';
// export namespace org.example.mynetwork{
   export enum ParcelStatus {
      PREPARING,
      DELIVERING,
      INSTOCK,
   }
   export class Parcel extends Asset {
      parcelId: string;
      description: string;
      maxQuantity: number;
      currQuantity: number;
      pStatus: ParcelStatus;
      units: Unit[];
      owner: Organization;
   }
   export enum UnitStatus {
      DELIVERING,
      INSTOCK,
      SELLING,
      SOLD,
   }
   export class Unit extends Asset {
      unitId: string;
      parcelId: string;
      description: string;
      createdDate: string;
      sellingDate: string;
      soldDate: string;
      uStatus: UnitStatus;
      owner: Organization;
   }
   export abstract class Organization extends Participant {
      orgId: string;
      name: string;
      country: string;
      phone: string;
      email: string;
   }
   export class Cooperative extends Organization {
      description: string;
   }
   export class TransportComp extends Organization {
      description: string;
   }
   export class Seller extends Organization {
      description: string;
   }
   export class Guest extends Participant {
      guestId: string;
   }
   export class AddUnitToParcel extends Transaction {
      parcel: Parcel;
      unit: Unit[];
      submittedBy: Organization;
   }
   export enum ShipperGender {
      MALE,
      FEMALE,
   }
   export class Trade extends Transaction {
      shipper_name: string;
      shipper_gender: ShipperGender;
      shipper_id: string;
      parcel: Parcel[];
      submittedBy: Organization;
      newOwner: Organization;
   }
   export class PutParcelIntoStock extends Transaction {
      parcel: Parcel[];
      submittedBy: Organization;
   }
   export class ForSale extends Transaction {
      unit: Unit[];
      submittedBy: Organization;
   }
   export class Sold extends Transaction {
      unit: Unit[];
      submittedBy: Organization;
   }
// }
