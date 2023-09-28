import express from 'express';
import  {login,getUserInfo} from '../controllers/user.js';
const router = express.Router();

router.post('/', login);

export default router;

