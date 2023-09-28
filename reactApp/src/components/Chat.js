import React from 'react';
import { Navigate, useNavigate } from "react-router-dom";
// import { AuthContext } from '../Context';
// import { useContext } from 'react';
// import { logggeduser } from './loggedUser';
// import { username } from "./Login.js"
import ConversationItem from './conversationItem.js'
//import MessageItem from './messageItem';
import {io} from "socket.io-client"
import MessageItem from './messageItem.js';


import { useState, useEffect } from 'react';
import AddContact from './addContact';
const socket = io("http://localhost:8080")

function Chat( { user, setUser, token, setToken } ) {
    
    
    
    const [currentChat, setcurrentChat] = useState('');
    const [currentImg, setcurrentImg] = useState('');
    const [messages, setmeesages] = useState('');
    const [texts, setTexts] = useState(false)
    const [conversations, setConversations] = useState([]);
    //const [getUser, setGetUser] = useState('');
    const [send, setSend] = useState(false)

    const [activeConversation, setactiveConversation] = useState(null)
    const [logOut, setLogOut] = React.useState(false);


    const fetchMessages = async () => {
        try {
          const response = await fetch('http://localhost:8080/api/Chats/1/Messages', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'authorization': 'Bearer ' + token // attach the token
            },
          });
    
          if (response.ok) {
            const msgs = await response.json();
            //setTime(TimeFormat(msgs[0].created).split('at'));
           
            setTexts(msgs);
          } else {
            console.error('Failed to fetch messages. Status:', response.status);
          }
        } catch (error) {
            alert('fun you')
          console.error('Error:', error);
        }
      };

      const renderMessages = () => {
        return texts.map((text, key) => (
          <MessageItem message={text} key={key} loggedUser={user} />
        ));
      };


    const initialize = async () => {
   
        
        const getcchats = await fetch('http://localhost:8080/api/Chats/', {
                'method': 'GET',
                'headers': {
                    'Content-Type': 'application/json',
                    'authorization': 'Bearer ' + token // attach the token
                },
            }
        )

        const conv = await getcchats.json();
        if (Object.keys(conv).length === 0 && conv.constructor == Object) 
            setConversations([]);
        else{
            setConversations(conv);
            //console.log(conv);
        }
            
    };

    useEffect(() => {
        socket.on('newMsg', ()=> {
          console.log("socket")
          var con = document.getElementById('conversations');
          //console.log(con)
          if(activeConversation) {
            
            var buttons = con.querySelectorAll('button');
            var loadButton = buttons[activeConversation.id - 1];
            loadButton.click();
            loadButton.click();
          }
          //loadConversation()
        });
      }, [socket])
    
        
    // if(texts) {
    //     fetchMessages()
    //     setmeesages(renderMessages());
    //     setTexts(false);
    // }
      


    useEffect(()=> {
        initialize();
    },[conversations]);

    

    useEffect(() => {
        if (activeConversation != null)
            setcurrentImg(activeConversation.img)
    }, [activeConversation]);
    const navigate = useNavigate();

    useEffect(() => {
        if (logOut) {
            navigate("/");
        }
    }, [logOut]);
    if (user == '') {
        navigate("/");
    }

    
    async function addMessage() {
        const text = document.getElementById("textmsg").value;
        if (text === ''|| currentChat ==='')
            return

        const res = await fetch('http://localhost:8080/api/Chats/' + activeConversation.id + '/Messages', {
                    method: 'post',
                    headers: {
                        'Content-Type': 'application/json',
                        'authorization': 'Bearer ' + token // attach the token
                    }, body: JSON.stringify({msg: text})
                }
            );

        document.getElementById("textmsg").value = ''
        socket.emit('send',activeConversation.id);
        
    }
    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            addMessage()
        }
    }
   
    
    
    const conversationsList = conversations && conversations.map((conversation, key) => (
        <ConversationItem conversation={conversation} key={key} 
            setcurrentChat={setcurrentChat} setcurrentImg={setcurrentImg} 
            setmeesages={setmeesages} setactiveConversation={setactiveConversation} loggedUser={user} 
            token={token} socket={socket} send={send} setTex={setTexts}/>
    ));
   
    return (<div style={{ display: 'inherit' }}>
        <div className="logout">
            <button className="btn btn-danger" id="logoutBtn" onClick={() => setLogOut(true)} >Logout</button>
        </div>

        <div id="container " className="mainchat chat-container">


            <div className="sidebar">
                <header className="status">
                    <div className="user">
                        <img src={user.profilePic} width="50px" />
                        {user.displayName}
                    </div>
                    <div className="adduser">
                        <AddContact setConversations={setConversations} conversationsList={conversationsList} conversations={conversations} setcurrentChat={setcurrentChat} setcurrentImg={setcurrentImg}
                            setmeesages={setmeesages} setactiveConversation={setactiveConversation} token={token}></AddContact>

                    </div>


                </header>

                <div className="modal fade" id="exampleModal" tabIndex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="exampleModalLabel">New chat</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <input className="new-chat" placeholder="contact's name" />
                                <button className="btn btn-secondary">Search</button>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" className="btn btn-primary">Add</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="conversations">

                    {conversationsList}
                </div>
            </div>
            <div className="chat">

                <div id="headline-chat">
                    <img src={currentImg} className="fred" />
                    {currentChat}
                </div>
                <div className="msg">
                
                    {messages}
                </div>

                <div className="in">
                    <input id="textmsg" placeholder="Your message" onKeyDown={handleKeyPress} />
                    <button onClick={addMessage} id="send" className="btn btn-primary">Send</button>
                </div>

            </div>
        </div >

        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
            integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
            crossOrigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.1/dist/umd/popper-base.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"
            integrity="sha384-B0UglyR+jN6IXJ1DkkaPvoyB1Ch/2QdJQqzkfO/V6ZI9KR3MlFvV8O1E82IufeY"
            crossOrigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
            crossOrigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/socket.io/4.2.0/socket.io.js"></script>
        <script src="/socket.io/socket.io.js"></script>



    </div >

    );
}

export default Chat;