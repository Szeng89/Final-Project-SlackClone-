import { IUser } from 'app/shared/model/user.model';
import { IWorkspace } from 'app/shared/model/workspace.model';
import { IChannel } from 'app/shared/model/channel.model';

export interface IUserProfile {
  id?: number;
  fullName?: string | null;
  timezone?: string | null;
  phone?: string | null;
  status?: string | null;
  user?: IUser | null;
  workspaces?: IWorkspace[] | null;
  channels?: IChannel[] | null;
}

export const defaultValue: Readonly<IUserProfile> = {};
