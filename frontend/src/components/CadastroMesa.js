import React, { useState, useEffect } from 'react';

const CadastrarMesa = () => {
    const [nomeRestaurante, setNomeRestaurante] = useState('');
    const [qtdAssentosMax, setQtdAssentosMax] = useState('');
    const [infoAdicional, setInfoAdicional] = useState('');
    const [restaurantes, setRestaurantes] = useState([]);
    const [message, setMessage] = useState('');

    useEffect(() => {
        const fetchRestaurantes = async () => {
            try {
                const response = await fetch('http://localhost:8080/restaurantes');
                if (response.ok) {
                    const data = await response.json();
                    setRestaurantes(data);
                } else {
                    throw new Error('Erro ao buscar restaurantes');
                }
            } catch (error) {
                console.error('Erro:', error);
            }
        };

        fetchRestaurantes();
    }, []);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const mesaData = {
            qtdAssentosMax: parseInt(qtdAssentosMax),
            infoAdicional: infoAdicional,
            restauranteId: nomeRestaurante
        };

        try {
            const response = await fetch('http://localhost:8080/mesas', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(mesaData)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage('Mesa cadastrada com sucesso!');
                console.log('Mesa cadastrada:', result);
            } else {
                throw new Error('Erro ao cadastrar a mesa');
            }
        } catch (error) {
            console.error('Erro:', error);
            setMessage('Não foi possível cadastrar a mesa. Verifique o console para mais detalhes.');
        }
    };

    return (
        <div>
            <h2>Cadastro de Mesa</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="nomeRestaurante">Nome do Restaurante:</label>
                    <select
                        id="nomeRestaurante"
                        value={nomeRestaurante}
                        onChange={(e) => setNomeRestaurante(e.target.value)}
                        required
                    >
                        <option value="">Selecione um restaurante</option>
                        {restaurantes.map((restaurante) => (
                            <option key={restaurante.id} value={restaurante.id}>
                                {restaurante.nome}
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label htmlFor="qtdAssentosMax">Quantidade Máxima de Assentos:</label>
                    <input
                        type="number"
                        id="qtdAssentosMax"
                        value={qtdAssentosMax}
                        onChange={(e) => setQtdAssentosMax(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="infoAdicional">Informações Adicionais:</label>
                    <input
                        type="text"
                        id="infoAdicional"
                        value={infoAdicional}
                        onChange={(e) => setInfoAdicional(e.target.value)}
                    />
                </div>
                <button type="submit">Cadastrar Mesa</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default CadastrarMesa;
