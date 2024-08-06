import React, { useState, useEffect } from 'react';
import axios from 'axios';

function MesaList() {
  const [mesas, setMesas] = useState([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    async function fetchMesas() {
      try {
        const response = await axios.get('http://localhost:8080/mesas');
        setMesas(response.data);
        setCarregando(false);
      } catch (error) {
        console.error('Erro ao buscar mesas:', error.response ? error.response.data : error.message);
        setCarregando(false);
      }
    }

    fetchMesas();
  }, []);

  if (carregando) {
    return <p>Carregando...</p>;
  }

  return (
    <div>
      <h1>Lista de Mesas</h1>
      <ul>
        {mesas.map(mesa => (
          <li key={mesa.id}>
            <strong>Quantidade de assentos:</strong> {mesa.qtdAssentosMax} -
            <strong> Informação Adicional:</strong> {mesa.infoAdicional} -
            <strong> Status:</strong> {mesa.status || 'Indefinido'} -
            <strong> Nome do Restaurante:</strong> {mesa.nomeRestaurante || 'Desconhecido'} -
            <strong> Pedidos:</strong> {mesa.pedidos ? mesa.pedidos.map((pedido, index) => (
              <span key={index}>{pedido}</span>
            )) : 'Nenhum pedido'}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default MesaList;
