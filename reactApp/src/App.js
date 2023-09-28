import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Login from "./components/Login"
import Register from "./components/Register";
import Chat from "./components/Chat";
import "./style.css";
import ContextProvider from "./Context";




function App() {
    const [user, setUser] = useState('');
    const [token, setToken] = useState('');

    

    return (
       // <ContextProvider>
            <div className="App" style={{ display: 'contents' }}>
                <Router>
                    <Routes>
                        <Route path="/" element={<Login setUser={setUser} setToken = {setToken} token = {token} />} />
                        <Route path="/Register" element={<Register user={user} setUser={setUser} setToken = {setToken} token = {token}/>} />
                        <Route path="/chat" element={<Chat user={user} setToken = {setToken} token = {token}/>} />
                    </Routes>
                </Router>
            </div>
        //</ContextProvider>

    );
}

export default App;
