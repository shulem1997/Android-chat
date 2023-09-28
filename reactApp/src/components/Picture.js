import React from 'react';
import { useState } from 'react';

function Picture(props) {

    function choosePic(event) {
        const file = event.target.files[0];
        const img = document.getElementById('userimg');

        if (file) {
            const url = URL.createObjectURL(file);
            img.src = url;
        }
        props.setSrc(event.target.files[0]);
    }


    return (
        
        <div className="mb-3 m-2">
            <label htmlFor="formFile" className="form-label">Profile picture</label>
            <input className="form-control" type="file" id="formFile" onChange={(event) => choosePic(event)} />
            <div className="under" id="profile" />


            <div className="form-group m-2">
                <img id="userimg" src={props.def} />
            </div>
        </div>
        
        );
}




export default Picture;