import { IWorkspace } from 'app/shared/model/workspace.model';
import { IMessage } from 'app/shared/model/message.model';
import { IUserProfile } from 'app/shared/model/user-profile.model';

export interface IChannel {
  id?: number;
  name?: string;
  description?: string | null;
  workspace?: IWorkspace | null;
  messages?: IMessage | null;
  members?: IUserProfile[] | null;
}

export const defaultValue: Readonly<IChannel> = {};
