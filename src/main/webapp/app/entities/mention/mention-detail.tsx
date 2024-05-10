import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './mention.reducer';

export const MentionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const mentionEntity = useAppSelector(state => state.mention.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="mentionDetailsHeading">
          <Translate contentKey="slackCloneTempApp.mention.detail.title">Mention</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{mentionEntity.id}</dd>
          <dt>
            <span id="userName">
              <Translate contentKey="slackCloneTempApp.mention.userName">User Name</Translate>
            </span>
          </dt>
          <dd>{mentionEntity.userName}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="slackCloneTempApp.mention.body">Body</Translate>
            </span>
          </dt>
          <dd>{mentionEntity.body}</dd>
          <dt>
            <Translate contentKey="slackCloneTempApp.mention.userProfile">User Profile</Translate>
          </dt>
          <dd>{mentionEntity.userProfile ? mentionEntity.userProfile.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/mention" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/mention/${mentionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MentionDetail;
