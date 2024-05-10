import { IMention } from 'app/shared/model/mention.model';
import { IUserProfile } from 'app/shared/model/user-profile.model';

export interface IMessage {
  id?: number;
  uploads?: string | null;
  pinned?: number | null;
  timestamp?: number | null;
  mentions?: IMention | null;
  userProfile?: IUserProfile | null;
}

export const defaultValue: Readonly<IMessage> = {};
