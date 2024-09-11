import React, { useState, useEffect } from 'react';

const CadastrarReserva = () => {
    const [quantidadePessoas, setQuantidadePessoas] = useState('');
    const [dataReserva, setDataReserva] = useState('');
    const [restaurantes, setRestaurantes] = useState([]);
    const [mesas, setMesas] = useState([]);
    const [restauranteId, setRestauranteId] = useState('');
    const [mesaId, setMesaId] = useState('');
    const [capacidadeMesa, setCapacidadeMesa] = useState(null);
    const [filteredMesas, setFilteredMesas] = useState([]);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchRestaurantes = async () => {
            try {
                const response = await fetch('http://localhost:8083/restaurantes');
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
                const response = await fetch('http://localhost:8082/mesas');
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

    useEffect(() => {
        if (mesaId) {
            const selectedMesa = mesas.find((mesa) => mesa.id === mesaId);
            if (selectedMesa) {
                setCapacidadeMesa(selectedMesa.qtdAssentosMax);
            }
        }
    }, [mesaId, mesas]);

    useEffect(() => {
        if (restauranteId) {
            const mesasDoRestaurante = mesas.filter((mesa) => mesa.restauranteId === restauranteId);
            setFilteredMesas(mesasDoRestaurante);
        } else {
            setFilteredMesas([]);
        }
    }, [restauranteId, mesas]);

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (parseInt(quantidadePessoas, 10) > capacidadeMesa) {
            setError('A quantidade de pessoas não pode ser maior que a capacidade máxima da mesa.');
            return;
        }

        setError('');

        const reservaData = {
            quantidadePessoas: parseInt(quantidadePessoas, 10),
            dataReserva,
            restauranteId,
            mesaId
        };

        try {
            const response = await fetch('http://localhost:8084/reservas', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reservaData)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage('Reserva cadastrada com sucesso!');
                console.log('Reserva cadastrada:', result);
            } else {
                throw new Error('Erro ao cadastrar a reserva');
            }
        } catch (error) {
            console.error('Erro:', error);
            setMessage('Não foi possível cadastrar a reserva. Verifique o console para mais detalhes.');
        }
    };

    return (
        <div>
            <h2>Cadastro de Reserva</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="quantidadePessoas">Quantidade de Pessoas:</label>
                    <input
                        type="number"
                        id="quantidadePessoas"
                        value={quantidadePessoas}
                        onChange={(e) => setQuantidadePessoas(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="dataReserva">Data da Reserva:</label>
                    <input
                        type="datetime-local"
                        id="dataReserva"
                        value={dataReserva}
                        onChange={(e) => setDataReserva(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="restauranteId">Restaurante:</label>
                    <select
                        id="restauranteId"
                        value={restauranteId}
                        onChange={(e) => setRestauranteId(e.target.value)}
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
                        onChange={(e) => setMesaId(e.target.value)}
                        required
                    >
                        <option value="">Selecione uma mesa</option>
                        {filteredMesas.map((mesa) => (
                            <option key={mesa.id} value={mesa.id}>
                                {mesa.infoAdicional} (Capacidade: {mesa.qtdAssentosMax})
                            </option>
                        ))}
                    </select>
                </div>
                <button type="submit">Cadastrar Reserva</button>
            </form>
            {message && <p>{message}</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default CadastrarReserva;
