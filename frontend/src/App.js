import React, { useState, useEffect } from 'react';
import './App.css';
import ListarMesas from './components/MesaList';
import CadastroRestaurante from './components/CadastroRestaurante';
import CadastrarMesa from './components/CadastroMesa';
import CadastrarPedido from './components/CadastroPedidos';
import CadastrarReserva from './components/CadastroReserva';
import Login from './components/Login';
import Register from './components/Register';

function App() {
    const [user, setUser] = useState(null);
    const [accountType, setAccountType] = useState(null);
    const [showLogin, setShowLogin] = useState(false);
    const [showRegister, setShowRegister] = useState(false);
    const [showListarMesas, setShowListarMesas] = useState(false);
    const [showCadastroRestaurante, setShowCadastroRestaurante] = useState(false);
    const [showCadastrarMesa, setShowCadastrarMesa] = useState(false);
    const [showCadastrarPedido, setShowCadastrarPedido] = useState(false);
    const [showCadastrarReserva, setShowCadastrarReserva] = useState(false);

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        const storedAccountType = localStorage.getItem('accountType');

        if (storedUser && storedAccountType) {
            setUser(storedUser);
            setAccountType(storedAccountType);
            setShowLogin(false);
            setShowRegister(false);
        } else {
            setShowLogin(true);
        }
    }, []);

    const handleLogin = (username, password, type) => {
        const accounts = JSON.parse(localStorage.getItem('accounts')) || {};
        const account = accounts[username];

        if (account && password === account.password && type === account.type) {
            setUser(username);
            setAccountType(type);
            localStorage.setItem('user', username);
            localStorage.setItem('accountType', type);
            setShowLogin(false);
            setShowRegister(false);
        } else {
            alert('Credenciais inválidas ou tipo de conta não corresponde.');
        }
    };

    const handleRegister = (username, password, type) => {
        let accounts = JSON.parse(localStorage.getItem('accounts')) || {};

        if (accounts[username]) {
            alert('Usuário já existe.');
            return;
        }

        accounts[username] = { password, type };
        localStorage.setItem('accounts', JSON.stringify(accounts));
        handleLogin(username, password, type);
    };

    const handleLogout = () => {
        setUser(null);
        setAccountType(null);
        localStorage.removeItem('user');
        localStorage.removeItem('accountType');
        setShowLogin(true);
        setShowRegister(false);
        setShowListarMesas(false);
        setShowCadastroRestaurante(false);
        setShowCadastrarMesa(false);
        setShowCadastrarPedido(false);
        setShowCadastrarReserva(false);
    };

    const renderButtons = () => {
        if (accountType === 'restaurante') {
            return (
                <>
                    <button onClick={() => {
                        setShowCadastroRestaurante(true);
                        setShowListarMesas(false);
                        setShowCadastrarMesa(false);
                        setShowCadastrarPedido(false);
                        setShowCadastrarReserva(false);
                    }}>
                        Cadastrar Restaurante
                    </button>
                    <button onClick={() => {
                        setShowCadastrarMesa(true);
                        setShowListarMesas(false);
                        setShowCadastroRestaurante(false);
                        setShowCadastrarPedido(false);
                        setShowCadastrarReserva(false);
                    }}>
                        Cadastrar Mesa
                    </button>
                    <button onClick={() => {
                        setShowListarMesas(true);
                        setShowCadastroRestaurante(false);
                        setShowCadastrarMesa(false);
                        setShowCadastrarPedido(false);
                        setShowCadastrarReserva(false);
                    }}>
                        Listar Mesas
                    </button>
                </>
            );
        }
        if (accountType === 'cliente') {
            return (
                <>
                    <button onClick={() => {
                        setShowCadastrarReserva(true);
                        setShowListarMesas(false);
                        setShowCadastroRestaurante(false);
                        setShowCadastrarMesa(false);
                        setShowCadastrarPedido(false);
                    }}>
                        Cadastrar Reserva
                    </button>
                    <button onClick={() => {
                        setShowCadastrarPedido(true);
                        setShowListarMesas(false);
                        setShowCadastroRestaurante(false);
                        setShowCadastrarMesa(false);
                        setShowCadastrarReserva(false);
                    }}>
                        Cadastrar Pedido
                    </button>
                    <button onClick={() => {
                        setShowListarMesas(true);
                        setShowCadastroRestaurante(false);
                        setShowCadastrarMesa(false);
                        setShowCadastrarPedido(false);
                        setShowCadastrarReserva(false);
                    }}>
                        Listar Mesas
                    </button>
                </>
            );
        }
        return null;
    };

    if (showLogin) {
        return (
            <div className="App">
                <h1>Gerenciamento de Restaurante</h1>
                <Login onLogin={handleLogin} />
                <button onClick={() => {
                    setShowLogin(false);
                    setShowRegister(true);
                }}>
                    Criar Conta
                </button>
            </div>
        );
    }

    if (showRegister) {
        return (
            <div className="App">
                <h1>Gerenciamento de Restaurante</h1>
                <Register onRegister={handleRegister} />
                <button onClick={() => {
                    setShowRegister(false);
                    setShowLogin(true);
                }}>
                    Voltar para Login
                </button>
            </div>
        );
    }

    return (
        <div className="App">
            <h1>Gerenciamento de Restaurante</h1>
            {user && (
                <div>
                    <h2>Bem-vindo, {user}!</h2>
                    <p>Tipo de Conta: {accountType}</p>
                </div>
            )}
            <button onClick={handleLogout}>Sair</button>
            {renderButtons()}
            {showListarMesas && <ListarMesas />}
            {showCadastroRestaurante && <CadastroRestaurante />}
            {showCadastrarMesa && <CadastrarMesa />}
            {showCadastrarPedido && <CadastrarPedido />}
            {showCadastrarReserva && <CadastrarReserva />}
        </div>
    );
}

export default App;
