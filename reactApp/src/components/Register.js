import React, { createContext } from 'react';
import { Navigate, useNavigate } from "react-router-dom";
import { useState, useEffect } from 'react';
import UserName from "./UserName";
import Password from "./Password";
import Picture from "./Picture";

import { useContext } from 'react';
import { AuthContext } from '../Context';
import '../style.css';


function Register({ user, setUser, token, setToken }) {

    const navigate = useNavigate();
    //const { users, setUsers } = useContext(AuthContext);
    //const { logggeduser, setlogggeduser } = createContext('osdv')
    const [userToken, setUserToken] = useState('')

    function password() {
        if (passwordVal.length < 8) {
            return false;
        }
        if (/\d/.test(passwordVal) === false) {
            return false;
        }
        if (passwordVal !== verValue)
            return false
        return true;
    }
    const saveData = async() => {
        // Create a new object with the current values
        if (username.trim() !== '' && displayName.trim() !== '' && password() && displayName !== '') {
            const password = passwordVal
            const profilePic = profilePic1.name
            const request = JSON.stringify({username, password, displayName, profilePic});
            //console.log(request)
            
            //console.log(profilePic)
            const res1 = await fetch('http://localhost:8080/api/Users/', {
            'method': 'post',
            'headers': {'Content-Type': 'application/json'},
            'body': request
         })

         if( await res1.status === 409) {
            alert("User's setails are unavailable");
            return;
         }
         if( !res1.ok) {
            alert("Something went wrong");
            return
         }

        
        const data = { 
            username, 
            password 
        };

        const res2 = await fetch('http://localhost:8080/api/Tokens/', {
            'method': 'post',
            'headers': {'Content-Type': 'application/json'},
            'body': JSON.stringify(data)
         })
         

        const token = await res2.text();
        setToken(token);
        setUser({username, password})
        navigate("/");
        return true;
        }
        alert('Please fill all fields properly')
        return false;
               
    }



    const [username, setUsername] = useState('');
    const [passwordVal, setPasswordVal] = useState('');
    const [verValue, setVerValue] = useState('');
    const [displayName, setDisplayName] = useState('');
    const [profilePic1, setprofilePic1] = useState('');





    function handleDisNameChange(event) {
        setDisplayName(event.target.value);
    }

    function handleUserNameChange(event) {

        setUsername(event.target.value);
    }

    function handlePasswordChange(event) {
        setPasswordVal(event.target.value);
    }

    function handleVerifyChange(event) {
        setVerValue(event.target.value);
    }

    function handlePicChange(event) {
        if (event.target.value) {
            setprofilePic1(event.target.value.name);     
        }
        else {
            setprofilePic1("default.png")
        }
    }

    return (
        <div className=" col-10 col-sm-9  col-md-8 col-lg-8  col-xl-6 " style={{ alignItems: 'center' }}>
            <div className=" m-auto display-2 head">
                Hello!
            </div>
            <form id="container">
                <UserName id="username" label="UserName" text="UserName" value={username} setValue={setUsername} onChange={handleUserNameChange} />
                <div className="under" id="userName" />

                <Password id="pass" label="Password" text="Password - at least 8 characters, containing digits" value={passwordVal} setValue={setPasswordVal} onChange={handlePasswordChange} />
                <div className="under" id="passwrd" />

                <Password id="verify" label="Verify Password" text="Verify Password" value={verValue} setValue={setVerValue} onChange={handleVerifyChange} />
                <div className="under" id="passwrdVer" />

                <UserName id="displayName" label="DisplayName" text="DisplayName" value={displayName} setValue={setDisplayName} onChange={handleDisNameChange} />
                <div className="under" id="display" />


                <Picture def="default.png" src={profilePic1} setSrc={setprofilePic1} onChange={handlePicChange} />


                <button id="register" onClick={saveData} type="button" className="btn btn-primary m-2">Register</button>
                <div className="form-group m-10">
                    <span className="m-10  ">Already registered?</span>
                    <a href="/" className="m-10">Click here to login</a>
                </div>
            </form>

        </div>
    );
}
export default Register;