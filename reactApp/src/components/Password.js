import React from 'react';
import { useState } from 'react';

function Password(props) {

    function onInputChange(event) {
        var y = document.getElementById('password');
        var x = y.value;
        var z = document.getElementById('passwrd');
        if (x != "") {
            if (x.length < 8) {
                z.innerHTML = "*Password should be 8 characters or more";
            }
            else if (/\d/.test(x) == false) {
                z.innerHTML = "*Password should contain digits";
            }
            else
                z.innerHTML = "";
        }
        else
            z.innerHTML = "";

        props.setValue(event.target.value);
    }

    return (
        <div className="form-group m-2">
            <label htmlFor="password">{props.label }</label>
            <input type="password" className="form-control" id="password" placeholder={props.text} value={props.value} onChange={onInputChange} />
            
        </div>
        
        );
}



export default Password;