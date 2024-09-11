import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function Notifications() {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const createClient = (url, topic, type) => {
            const socket = new SockJS(url);
            const client = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {},
                debug: function (str) {
                    console.log('STOMP: ' + str);
                },
                onConnect: () => {
                    console.log(`Connected to ${url}`);
                    client.subscribe(topic, (message) => {
                        if (message.body) {
                            console.log(`Received ${type} message:`, message.body);
                            const notification = { type, content: JSON.parse(message.body) };
                            setNotifications((prevNotifications) => [...prevNotifications, notification]);

                            setTimeout(() => {
                                setNotifications((prevNotifications) =>
                                    prevNotifications.filter((notif) => notif !== notification)
                                );
                            }, 5000);
                        }
                    });
                },
                onStompError: (frame) => {
                    console.error('Broker reported error: ', frame.headers['message']);
                    console.error('Additional details: ', frame.body);
                },
            });

            client.activate();

            return client;
        };

        const clients = [
            createClient('http://localhost:8082/ws', '/topic/mesaCadastrada', 'Mesa Cadastrada'),
            createClient('http://localhost:8083/ws', '/topic/restauranteCadastrado', 'Restaurante Criado'),
            createClient('http://localhost:8084/ws', '/topic/mesaReservada', 'Mesa Reservada'),
            createClient('http://localhost:8085/ws', '/topic/pedidoCriado', 'Pedido Criado')
        ];

        return () => {
            clients.forEach(client => client.deactivate());
        };
    }, []);

    return (
        <div className="notifications">
            {notifications.length > 0 && (
                <div className="notification-popup">
                    <h3>Notificações</h3>
                    <ul>
                        {notifications.map((notif, index) => (
                            <li key={index}>
                                {notif.type === 'Mesa Cadastrada' && `Mesa do restaurante foi criada com sucesso!`}
                                {notif.type === 'Mesa Reservada' && `Mesa foi reservada com sucesso!`}
                                {notif.type === 'Pedido Criado' && `Pedido Criado com sucesso!`}
                                {notif.type === 'Restaurante Criado' && `Restaurante Criado com sucesso!`}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

export default Notifications;
