import express from 'express';
import  {register} from '../controllers/user.js';
import  {getUserChats, getUserMessages, addUserMessages,addUserChat} from '../controllers/user.js';

const router = express.Router();

//router.post('/', register);
router.get('/', getUserChats);
router.get('/:id/Messages', getUserMessages);
router.post('/:id/Messages', addUserMessages);
router.post('/', addUserChat);
export default router;

