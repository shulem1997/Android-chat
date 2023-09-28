import React from 'react';
import { createContext, useState } from 'react';

export const AuthContext = createContext();

const ContextProvider = (props) => {
    const [users, setUsers] = useState([]);

    return (
        <AuthContext.Provider value={{users, setUsers}} >{props.children}</AuthContext.Provider>
    );
};

export default ContextProvider;