import React from 'react';
import { useState } from 'react';

function UserName(props) {
    

    function handleChange(event) {
        props.setValue(event.target.value);    
    }

    return (
        <div className="form-group m-2 ">
            <label htmlFor="username">{props.label}</label>
            <input
                type="text"
                className="form-control"
                placeholder={props.text}
                value={props.value}
                onChange={handleChange}
            />
        </div>
    );
}

export default UserName;