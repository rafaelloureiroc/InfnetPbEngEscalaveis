import React, { useState, useEffect } from 'react';

const CadastrarPedido = () => {
    const [descricaoPedido, setDescricaoPedido] = useState('');
    const [valorTotal, setValorTotal] = useState('');
    const [restaurantes, setRestaurantes] = useState([]);
    const [mesas, setMesas] = useState([]);
    const [mesasFiltradas, setMesasFiltradas] = useState([]);
    const [restauranteId, setRestauranteId] = useState('');
    const [mesaId, setMesaId] = useState('');
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

        const fetchMesas = async () => {
            try {
                const response = await fetch('http://localhost:8080/mesas');
                if (response.ok) {
                    const data = await response.json();
                    setMesas(data);
                } else {
                    throw new Error('Erro ao buscar mesas');
                }
            } catch (error) {
                console.error('Erro:', error);
            }
        };

        fetchRestaurantes();
        fetchMesas();
    }, []);

    const handleRestauranteChange = (event) => {
        const selectedRestauranteId = event.target.value;
        setRestauranteId(selectedRestauranteId);

        const mesasFiltradas = mesas.filter(mesa => mesa.restauranteId === selectedRestauranteId);
        setMesasFiltradas(mesasFiltradas);

        setMesaId('');
    };

    const handleMesaChange = (event) => {
        const selectedMesaId = event.target.value;
        setMesaId(selectedMesaId);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const pedidoData = {
            descricaoPedido,
            valorTotal: parseFloat(valorTotal),
            restauranteId,
            mesaId
        };

        try {
            const response = await fetch('http://localhost:8080/pedidos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(pedidoData)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage('Pedido cadastrado com sucesso!');
                console.log('Pedido cadastrado:', result);
            } else {
                throw new Error('Erro ao cadastrar o pedido');
            }
        } catch (error) {
            console.error('Erro:', error);
            setMessage('Não foi possível cadastrar o pedido. Verifique o console para mais detalhes.');
        }
    };

    return (
        <div>
            <h2>Cadastro de Pedido</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="descricaoPedido">Descrição do Pedido:</label>
                    <input
                        type="text"
                        id="descricaoPedido"
                        value={descricaoPedido}
                        onChange={(e) => setDescricaoPedido(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="valorTotal">Valor Total:</label>
                    <input
                        type="number"
                        id="valorTotal"
                        value={valorTotal}
                        onChange={(e) => setValorTotal(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="restauranteId">Restaurante:</label>
                    <select
                        id="restauranteId"
                        value={restauranteId}
                        onChange={handleRestauranteChange}
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
                    <label htmlFor="mesaId">Mesa:</label>
                    <select
                        id="mesaId"
                        value={mesaId}
                        onChange={handleMesaChange}
                        required
                    >
                        <option value="">Selecione uma mesa</option>
                        {mesasFiltradas.map((mesa) => (
                            <option key={mesa.id} value={mesa.id}>
                                {mesa.infoAdicional}
                            </option>
                        ))}
                    </select>
                </div>
                <button type="submit">Cadastrar Pedido</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default CadastrarPedido;
