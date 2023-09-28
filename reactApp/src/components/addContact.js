import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import ConversationItem from './conversationItem';

function AddContact({ setConversations, conversations, conversationsList, setcurrentChat, setcurrentImg, setmeesages, setactiveConversation, token }) {
    const [show, setShow] = useState(false);

    const handleClose = async() => {
        
        const text = document.getElementById('addName');
        const contact = text.value
        
        if(contact == "") {
            setShow(false);
            return
        }

        const res = await fetch('http://localhost:8080/api/Chats', {
            method: 'post',
            headers: {
                'Content-Type': 'application/json',
                'authorization': 'Bearer ' + token, // attach the token

            }, body: JSON.stringify({username: contact})
        });
        await res;
        setShow(false);
        //setConversations(...conversations, )

    }
        
    const handleShow = () => setShow(true);

    return (
        <>
            <button type="button" className="btn addcontact" onClick={handleShow}>
                <img src="add-user.svg" className="addimg" />
            </button>

            <Modal show={show} onHide={()=>{setShow(false)}}>
                <Modal.Header closeButton>
                    <Modal.Title>Add Contact</Modal.Title>
                </Modal.Header>
                <input placeholder='contacts name' id='addName'></input>
                <Modal.Footer>
                    <Button variant="secondary" onClick={()=>{setShow(false)}}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={(handleClose)}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default AddContact