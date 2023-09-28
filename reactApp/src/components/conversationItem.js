import React, { useState, useEffect } from "react";
import MessageItem from './messageItem.js';
import {io} from "socket.io-client"

function ConversationItem({ conversation, setcurrentChat, setcurrentImg, setmeesages, setactiveConversation, loggedUser, token, socket, setTex }) {
  let { id, user, lastMsg } = conversation;
  const [messagesArr, setMessages] = useState([]);
  const [time, setTime] = useState('');

  useEffect(() => {
    fetchMessages();
  }, []);

  // useEffect(() => {
  //   socket.on('newMsg', ()=> {
  //     console.log("socket")
  //     loadConversation()
  //     //loadConversation()
  //     setTex(true)

  //   });
  // }, [socket]);



  function TimeFormat(time) {
    const date = new Date(time);
    const options = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    };
    return date.toLocaleString('en-US', options);
  }

  const fetchMessages = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/Chats/${id}/Messages`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'authorization': 'Bearer ' + token // attach the token
        },
      });

      if (response.ok) {
        const msgs = await response.json();
        setTime(TimeFormat(msgs[0].created).split('at'));
        
        setMessages(msgs);
      } else {
        console.error('Failed to fetch messages. Status:', response.status);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const renderMessages = () => {
    return messagesArr.map((message, key) => (
      <MessageItem message={message} key={key} loggedUser={loggedUser} />
    ));
  };

  const loadConversation = () => {
    //console.log(conversation.id)
    setactiveConversation(conversation);
    setcurrentChat(user.displayName);
    setcurrentImg(user.profilePic);
    fetchMessages();
    setmeesages(renderMessages());
  };

  return (
    <button className="open-chat" onClick={loadConversation}>
      <img src={user.profilePic} alt={user.displayName} />
      {user.displayName}
      <div className="chat-time">
        {time[0]}<br></br>
        {time[1]}
      </div>
    </button>
  );
}

export default ConversationItem;
