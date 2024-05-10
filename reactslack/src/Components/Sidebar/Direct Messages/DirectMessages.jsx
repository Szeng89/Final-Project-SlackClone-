import React, { useState } from 'react';

const PrivateChat = () => {
  const [privateChats, setPrivateChats] = useState([]);
  const [newPrivateChatName, setNewPrivateChatName] = useState("");

  const handleAddPrivateChat = () => {
    if (newPrivateChatName.trim() === "") {
      return;
    }
    const newPrivateChat = {
      id: privateChats.length + 1,
      name: newPrivateChatName
    };
    setPrivateChats([...privateChats, newPrivateChat]);
    setNewPrivateChatName("");
  };

  const handleDeletePrivateChat = (chatId) => {
    const updatedPrivateChats = privateChats.filter(chat => chat.id !== chatId);
    setPrivateChats(updatedPrivateChats);
  };

  return (
    <div>
      <h2>Direct Messages</h2>
      <ul>
        {/* Render list of private chats */}
        {privateChats.map(chat => (
          <li key={chat.id}>
            <span>{chat.name}</span>
            <button onClick={() => handleDeletePrivateChat(chat.id)}>Delete</button>
          </li>
        ))}
      </ul>
      <div className="add-channel">
        <input
          type="text"
          placeholder="Enter direct message name"
          value={newPrivateChatName}
          onChange={(e) => setNewPrivateChatName(e.target.value)}
        />
        <button onClick={handleAddPrivateChat}>+</button>
      </div>
    </div>
  );
}

export default PrivateChat;
