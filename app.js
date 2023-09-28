import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors'
import userLogin from './routes/userLogin.js';
import userRegister from './routes/userRegister.js';
import userChats from './routes/chats.js';

import http from 'http'
import {Server} from "socket.io";


import mongoose from 'mongoose'
import { MongoClient } from 'mongodb';
import { Socket } from 'dgram';


const server = express();



server.get('/Register', (req, res) => {
    res.sendFile('index.html', { root: 'public' });
});
var users;
const client = new MongoClient("mongodb://127.0.0.1:27017");
try {
    const db = client.db("ChatWeb");
     users=db.collection("Users");
    
    
} catch (error) {
    await client.close();
    
}
server.use(bodyParser.json());
server.use(express.static('public'));
server.use(cors({
    origin: 'http://localhost:3000',
    optionsSuccessStatus: 200 // some legacy browsers (IE11, various SmartTVs) choke on 204
  }));
server.use(bodyParser.urlencoded({ extended: true }));
 server.set('view engine', 'ejs');

server.use('/api/Tokens', userLogin);
server.use('/api/Users', userRegister);
server.use('/api/Chats', userChats);


const http_server = http.createServer(server);
const io = new Server(http_server, {
  cors: {
    origin: "http://localhost:3000"
  }
});



http_server.listen(8080);

io.on("connection", (socket) => {
  
  
  socket.on('send', ()=> {  
    socket.broadcast.emit('newMsg')
  })
});


export default users;