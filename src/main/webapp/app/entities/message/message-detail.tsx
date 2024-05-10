import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './message.reducer';

export const MessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const messageEntity = useAppSelector(state => state.message.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="messageDetailsHeading">
          <Translate contentKey="slackCloneTempApp.message.detail.title">Message</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{messageEntity.id}</dd>
          <dt>
            <span id="uploads">
              <Translate contentKey="slackCloneTempApp.message.uploads">Uploads</Translate>
            </span>
          </dt>
          <dd>{messageEntity.uploads}</dd>
          <dt>
            <span id="pinned">
              <Translate contentKey="slackCloneTempApp.message.pinned">Pinned</Translate>
            </span>
          </dt>
          <dd>{messageEntity.pinned}</dd>
          <dt>
            <span id="timestamp">
              <Translate contentKey="slackCloneTempApp.message.timestamp">Timestamp</Translate>
            </span>
          </dt>
          <dd>{messageEntity.timestamp}</dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.message.mentions">Mentions</Translate>
          </dt>
          <dd>{messageEntity.mentions ? messageEntity.mentions.id : ''}</dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.message.userProfile">User Profile</Translate>
          </dt>
          <dd>{messageEntity.userProfile ? messageEntity.userProfile.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/message/${messageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MessageDetail;
