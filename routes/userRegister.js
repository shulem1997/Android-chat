import express from 'express';
import  {register} from '../controllers/user.js';
import  {login,getUserInfo} from '../controllers/user.js';

const router = express.Router();

router.post('/', register);
router.get('/:id', getUserInfo);
export default router;

