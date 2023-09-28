import React from 'react';
import { Navigate, useNavigate } from "react-router-dom";
import { useState, useEffect } from 'react';
import UserName from "./UserName";
import Password from "./Password";
import { useContext } from 'react';
import { AuthContext } from '../Context';


function Login({ user, setUser, token, setToken }) {

    //const { users, setUsers } = useContext(AuthContext);
    

    const [username, setUsername] = useState('');
    const [passwordVal, setPasswordVal] = useState('');
    
    const [val, setVal] = React.useState(false);
    
    const navigate = useNavigate();
    useEffect(() => {
        if (val) {
            return (<Navigate to="/Chat" />);
        }
    }, [val]);


    function handleUserNameChange(event) {

        setUsername(event.target.value);
    }

    function handlePasswordChange(event) {
        setPasswordVal(event.target.value);
    }

    // async function checkUserExists(username, password,token) {
        
    //     const data = {username, password}

    //     const res = await fetch('http://localhost:5000/api/Tokens/', {
    //     'method': 'post',
    //     'headers': {'Content-Type': 'application/json'}, 
    //     'body': JSON.stringify(data)
    //     }
    //     )

    //     if(res.ok) {
    //         const token = await res.text();
    //         console.log("res is ok login");
    //         return {username, password, token};
    //     }
    //     else{
    //         console.log("res is not ok LOGIN");
    //         return null;
    //     }
       
    // }


    const clicklogin = async () => {

        const password = passwordVal
        // const myItem = localStorage.getItem(username);//get the username that the user enter and check if the
        //the password on storage is the right one
        const userJSON2 = JSON.stringify({username, password});
        //send the data to the sever
        //get the token from the server by username and password the user enter
        const res2 = await fetch('http://localhost:8080/api/Tokens', {
            'method': 'post',
            'headers': {
                'Content-Type': 'application/json'
            },
            'body': userJSON2
        })
        if (res2.status === 404) {//if dont find the user so cant make token
            alert("Incorrect username and/or password")
            return;
        }
        const jsonResponse = await res2.json()
        const token= jsonResponse.token;
        const userJSON = JSON.stringify({username, password});
        const res = await fetch('http://localhost:8080/api/Users/'+username, {
            'method': 'get',
            'headers': {
                "authorization": "Bearer "+JSON.stringify(token),
                'Content-Type': 'application/json'
            },
        })
        if (res.status === 404) {//if dont find the user so cant make token
            alert("Incorrect username and/or password")
            return;
        }
        //set the token and save the username
        
    
        const jsonResponseDetaeil = await res.text();
        //alert(jsonResponseDetaeil)
        const newLogin = JSON.parse(jsonResponseDetaeil);
        setUser(newLogin)//if the user login, save the username and go to chat
        setToken(JSON.stringify(token))
        navigate('/chat'); //move to chat
    }



    // async function loginClick() {
    //     var password= passwordVal
    //     var displayName=null
    //     var profilePic=null
    //     const data = {username, password,displayName,profilePic}
    //     const login = new Promise((resolve, reject) => {
       

    //     const res =  fetch('http://localhost:5000/api/Tokens/', {
    //     'method': 'post',
    //     'headers': {'Content-Type': 'application/json'}, 
    //     'body': JSON.stringify({username, password})
    //     }
    //     )

    //     if(res.ok) {
    //         setToken(res.text());
    //         console.log("res is ok login");
    //          JSON.stringify({username, password})
            
    //     }
    //     console.log("res is not ok LOGIN");
    //         resolve(); // fulfilled
    //         reject(); // rejected
    //     });
    //     const getUser = new Promise((resolve, reject) => {
    //         const res =  fetch('http://localhost:5000/api/Users/'+username, {
    //         'method': 'post',
    //         'headers': {'Content-Type': 'application/json'}, 
    //         'body': JSON.stringify({username, displayName,profilePic})
    //         }
    //         )
    
    //         if(res.ok) {
    //             setToken(res.text());
    //             console.log("res is ok login");
    //              JSON.stringify({username, displayName,profilePic})
                
    //         }
    //         console.log("res is not ok LOGIN");
    //             resolve(); // fulfilled
    //             reject(); // rejected
    //         });
    //     login
    // .then(() => {
      
    // })
    // .catch(() => {
    //     // do something when the promise is rejected
    // });
    // getUser
    // .then(() => {
      
    //     setUser({data,displayName, token})

    //     navigate("/Chat")
    // })
    // .catch(() => {
    //     // do something when the promise is rejected
    // });
       
    // }

    
    return (
        <div className="Login" style={{ display: 'inherit'  , alignItems:'center'}}>
            <div className="col-11 col-sm-10 col-md-9 col-lg-8 col-xl-5" style={{ marginTop: '8%' }}>
                <div className="display-2 head">
                    Hello!
                </div>
                <form id="container">
                    <UserName id="username" label="UserName" text="UserName" value={username} setValue={setUsername} onChange={handleUserNameChange} />

                    <Password id="password"
                        label="Password"
                        text="Password"
                        value={passwordVal}
                        setValue={setPasswordVal}
                        onChange={handlePasswordChange} />

                    <div className="under" id="passwrd" />
                    <button className="btn btn-primary" type = "button" onClick={clicklogin}>Login</button>
                    <div className="form-group m-3">
                        <span className="m-10">Not submitted yet?</span>
                        <a href="Register" id = "link-To-register" className="m-10">Click here to register</a>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Login;