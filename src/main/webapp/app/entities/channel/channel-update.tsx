import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IWorkspace } from 'app/shared/model/workspace.model';
import { getEntities as getWorkspaces } from 'app/entities/workspace/workspace.reducer';
import { IMessage } from 'app/shared/model/message.model';
import { getEntities as getMessages } from 'app/entities/message/message.reducer';
import { IUserProfile } from 'app/shared/model/user-profile.model';
import { getEntities as getUserProfiles } from 'app/entities/user-profile/user-profile.reducer';
import { IChannel } from 'app/shared/model/channel.model';
import { getEntity, updateEntity, createEntity, reset } from './channel.reducer';

export const ChannelUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const workspaces = useAppSelector(state => state.workspace.entities);
  const messages = useAppSelector(state => state.message.entities);
  const userProfiles = useAppSelector(state => state.userProfile.entities);
  const channelEntity = useAppSelector(state => state.channel.entity);
  const loading = useAppSelector(state => state.channel.loading);
  const updating = useAppSelector(state => state.channel.updating);
  const updateSuccess = useAppSelector(state => state.channel.updateSuccess);

  const handleClose = () => {
    navigate('/channel');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getWorkspaces({}));
    dispatch(getMessages({}));
    dispatch(getUserProfiles({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...channelEntity,
      ...values,
      workspace: workspaces.find(it => it.id.toString() === values.workspace?.toString()),
      messages: messages.find(it => it.id.toString() === values.messages?.toString()),
      members: mapIdList(values.members),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...channelEntity,
          workspace: channelEntity?.workspace?.id,
          messages: channelEntity?.messages?.id,
          members: channelEntity?.members?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="slackCloneTempApp.channel.home.createOrEditLabel" data-cy="ChannelCreateUpdateHeading">
            <Translate contentKey="slackCloneTempApp.channel.home.createOrEditLabel">Create or edit a Channel</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="channel-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('slackCloneTempApp.channel.name')}
                id="channel-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('slackCloneTempApp.channel.description')}
                id="channel-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                id="channel-workspace"
                name="workspace"
                data-cy="workspace"
                label={translate('slackCloneTempApp.channel.workspace')}
                type="select"
              >
                <option value="" key="0" />
                {workspaces
                  ? workspaces.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="channel-messages"
                name="messages"
                data-cy="messages"
                label={translate('slackCloneTempApp.channel.messages')}
                type="select"
              >
                <option value="" key="0" />
                {messages
                  ? messages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('slackCloneTempApp.channel.members')}
                id="channel-members"
                data-cy="members"
                type="select"
                multiple
                name="members"
              >
                <option value="" key="0" />
                {userProfiles
                  ? userProfiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/channel" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ChannelUpdate;
