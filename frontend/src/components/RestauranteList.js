import React, { useState, useEffect } from 'react';
import axios from 'axios';

const RestauranteList = () => {
    const [restaurantes, setRestaurantes] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState('');

    useEffect(() => {
        async function fetchRestaurantes() {
            try {
                const response = await axios.get('http://localhost:8083/restaurantes');
                setRestaurantes(response.data);
                setCarregando(false);
            } catch (error) {
                console.error('Erro ao buscar restaurantes:', error);
                setErro('Não foi possível carregar a lista de restaurantes. Verifique o console para mais detalhes.');
                setCarregando(false);
            }
        }

        fetchRestaurantes();
    }, []);

    if (carregando) {
        return <p>Carregando...</p>;
    }

    return (
        <div>
            <h1>Lista de Restaurantes</h1>
            {erro && <p>{erro}</p>}
            <ul>
                {restaurantes.map(restaurante => (
                    <li key={restaurante.id}>
                        <strong>Nome:</strong> {restaurante.nome} -
                        <strong> CEP:</strong> {restaurante.cep}
                        <strong> Logradouro:</strong> {restaurante.logradouro}
                        <strong> Bairro:</strong> {restaurante.bairro}
                        <strong> Cidade:</strong> {restaurante.cidade}
                        <strong> Uf:</strong> {restaurante.uf}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RestauranteList;
