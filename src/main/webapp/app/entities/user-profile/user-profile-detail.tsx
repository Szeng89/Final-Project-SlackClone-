import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-profile.reducer';

export const UserProfileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userProfileEntity = useAppSelector(state => state.userProfile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userProfileDetailsHeading">
          <Translate contentKey="slackCloneTempApp.userProfile.detail.title">UserProfile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.id}</dd>
          <dt>
            <span id="fullName">
              <Translate contentKey="slackCloneTempApp.userProfile.fullName">Full Name</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.fullName}</dd>
          <dt>
            <span id="timezone">
              <Translate contentKey="slackCloneTempApp.userProfile.timezone">Timezone</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.timezone}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="slackCloneTempApp.userProfile.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.phone}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="slackCloneTempApp.userProfile.status">Status</Translate>
            </span>
          </dt>
          <dd>{userProfileEntity.status}</dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.userProfile.user">User</Translate>
          </dt>
          <dd>{userProfileEntity.user ? userProfileEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.userProfile.workspaces">Workspaces</Translate>
          </dt>
          <dd>
            {userProfileEntity.workspaces
              ? userProfileEntity.workspaces.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {userProfileEntity.workspaces && i === userProfileEntity.workspaces.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.userProfile.channels">Channels</Translate>
          </dt>
          <dd>
            {userProfileEntity.channels
              ? userProfileEntity.channels.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {userProfileEntity.channels && i === userProfileEntity.channels.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/user-profile" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-profile/${userProfileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserProfileDetail;
