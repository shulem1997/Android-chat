import React from "react";


function MessageItem({ message, loggedUser }) {

    function TimeFormat(time) {
        const date = new Date(time);
        const options = { 
           
          hour: 'numeric', 
          minute: 'numeric',  
        };
        return date.toLocaleString('en-US', options);
      }
    



   const {id, created, sender, content}= message;
   const time = TimeFormat(created);
    if (sender.username === loggedUser.username)
    return (
        <div className="onetxt">
            <span className="from">{content}<br></br>{time}</span>
        </div>
    );
    else
    return (
        <div className="onetxt">
            <span className="to">{content}<br></br>{time}</span>
        </div>
    );
}

export default MessageItem;
