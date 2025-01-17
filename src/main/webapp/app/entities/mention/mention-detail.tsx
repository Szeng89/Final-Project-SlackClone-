import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
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
        <h2 data-cy="mentionDetailsHeading">Mention</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{mentionEntity.id}</dd>
          <dt>
            <span id="userName">User Name</span>
          </dt>
          <dd>{mentionEntity.userName}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{mentionEntity.text}</dd>
        </dl>
        <Button tag={Link} to="/mention" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/mention/${mentionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MentionDetail;
