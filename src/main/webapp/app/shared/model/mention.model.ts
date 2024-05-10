import { IUserProfile } from 'app/shared/model/user-profile.model';

export interface IMention {
  id?: number;
  userName?: string;
  body?: string;
  userProfile?: IUserProfile | null;
}

export const defaultValue: Readonly<IMention> = {};
