import React, { useState } from 'react';

const Register = ({ onRegister }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [accountType, setAccountType] = useState('cliente');

    const handleRegister = (e) => {
        e.preventDefault();
        onRegister(username, password, accountType);
    };

    return (
        <div>
            <h2>Cadastro</h2>
            <form onSubmit={handleRegister}>
                <div>
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="accountType">Tipo de Conta:</label>
                    <select
                        id="accountType"
                        value={accountType}
                        onChange={(e) => setAccountType(e.target.value)}
                    >
                        <option value="cliente">Cliente</option>
                        <option value="restaurante">Restaurante</option>
                    </select>
                </div>
                <button type="submit">Cadastrar</button>
            </form>
        </div>
    );
};

export default Register;
