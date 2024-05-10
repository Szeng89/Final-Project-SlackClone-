import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserProfile from './user-profile';
import Channel from './channel';
import Workspace from './workspace';
import Message from './message';
import Mention from './mention';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="user-profile/*" element={<UserProfile />} />
        <Route path="channel/*" element={<Channel />} />
        <Route path="workspace/*" element={<Workspace />} />
        <Route path="message/*" element={<Message />} />
        <Route path="mention/*" element={<Mention />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
