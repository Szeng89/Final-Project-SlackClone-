import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './user-profile.reducer';

export const UserProfile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const userProfileList = useAppSelector(state => state.userProfile.entities);
  const loading = useAppSelector(state => state.userProfile.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="user-profile-heading" data-cy="UserProfileHeading">
        User Profiles
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/user-profile/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new User Profile
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {userProfileList && userProfileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('email')}>
                  Email <FontAwesomeIcon icon={getSortIconByFieldName('email')} />
                </th>
                <th className="hand" onClick={sort('timezone')}>
                  Timezone <FontAwesomeIcon icon={getSortIconByFieldName('timezone')} />
                </th>
                <th className="hand" onClick={sort('phone')}>
                  Phone <FontAwesomeIcon icon={getSortIconByFieldName('phone')} />
                </th>
                <th>
                  Messages <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Workspaces <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Channels <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userProfileList.map((userProfile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/user-profile/${userProfile.id}`} color="link" size="sm">
                      {userProfile.id}
                    </Button>
                  </td>
                  <td>{userProfile.name}</td>
                  <td>{userProfile.email}</td>
                  <td>{userProfile.timezone}</td>
                  <td>{userProfile.phone}</td>
                  <td>{userProfile.messages ? <Link to={`/message/${userProfile.messages.id}`}>{userProfile.messages.id}</Link> : ''}</td>
                  <td>
                    {userProfile.workspaces
                      ? userProfile.workspaces.map((val, j) => (
                          <span key={j}>
                            <Link to={`/workspace/${val.id}`}>{val.id}</Link>
                            {j === userProfile.workspaces.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td>
                    {userProfile.channels
                      ? userProfile.channels.map((val, j) => (
                          <span key={j}>
                            <Link to={`/channel/${val.id}`}>{val.id}</Link>
                            {j === userProfile.channels.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/user-profile/${userProfile.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`/user-profile/${userProfile.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/user-profile/${userProfile.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No User Profiles found</div>
        )}
      </div>
    </div>
  );
};

export default UserProfile;
