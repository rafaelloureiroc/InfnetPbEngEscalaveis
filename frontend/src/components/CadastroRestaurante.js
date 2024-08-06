import React, { useState } from 'react';

const CadastroRestaurante = () => {
    const [nome, setNome] = useState('');
    const [localizacao, setLocalizacao] = useState('');
    const [cep, setcep] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();

        const restauranteData = {
            nome: nome,
            localizacao: localizacao,
            cep : cep
        };

        try {
            const response = await fetch('http://localhost:8080/restaurantes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(restauranteData)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage('Restaurante cadastrado com sucesso!');
                console.log('Restaurante cadastrado:', result);
            } else {
                throw new Error('Erro ao cadastrar o restaurante');
            }
        } catch (error) {
            console.error('Erro:', error);
            setMessage('Não foi possível cadastrar o restaurante. Verifique o console para mais detalhes.');
        }
    };

    return (
        <div>
            <h1>Cadastro de Restaurante</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="nome ">Nome:</label>
                    <input
                        type="text"
                        id="nome "
                        value={nome}
                        onChange={(e) => setNome(e.target.value)}
                        required
                    />
                </div>
                <div>
                       <label htmlFor="cep ">Cep:</label>
                       <input
                       type="number"
                       id="cep "
                       value={cep}
                       onChange={(e) => setcep(e.target.value)}
                       required
                                    />
                </div>
                <button type="submit">Cadastrar Restaurante</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default CadastroRestaurante;
