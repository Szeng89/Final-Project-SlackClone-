import React from 'react';
import Jane from './Assets/Chris.jpg';
import profile from './Assets/profile.jpg';

const Message = () => {
  return (
    <div className="message owner">
      <div className="messageInfo">
        <img src={Jane} alt="" />
        <span>just now</span>
      </div>
      <div className="messageContent">
        <p>hello</p>
        <img src={profile} alt="Profile" />
      </div>
    </div>
  );
};



export default Message;
