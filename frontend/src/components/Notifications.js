import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function Notifications() {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {},
            debug: function (str) {
                console.log('STOMP: ' + str);
            },
            onConnect: () => {
                console.log('Connected to WebSocket');

                client.subscribe('/topic/mesaCadastrada', (message) => {
                    if (message.body) {
                        console.log('Received mesaCadastrada message:', message.body);
                        const notification = { type: 'Mesa Cadastrada', content: JSON.parse(message.body) };
                        setNotifications((prevNotifications) => [...prevNotifications, notification]);

                        setTimeout(() => {
                            setNotifications((prevNotifications) =>
                                prevNotifications.filter((notif) => notif !== notification)
                            );
                        }, 5000);
                    }
                });

                client.subscribe('/topic/mesaReservada', (message) => {
                    if (message.body) {
                        console.log('Received mesaReservada message:', message.body);
                        const notification = { type: 'Mesa Reservada', content: JSON.parse(message.body) };
                        setNotifications((prevNotifications) => [...prevNotifications, notification]);

                        setTimeout(() => {
                            setNotifications((prevNotifications) =>
                                prevNotifications.filter((notif) => notif !== notification)
                            );
                        }, 5000);
                    }
                });

                client.subscribe('/topic/pedidoCriado', (message) => {
                    if (message.body) {
                        console.log('Received pedidoCriado message:', message.body);
                        const notification = { type: 'Pedido Criado', content: JSON.parse(message.body) };
                        setNotifications((prevNotifications) => [...prevNotifications, notification]);

                        setTimeout(() => {
                            setNotifications((prevNotifications) =>
                                prevNotifications.filter((notif) => notif !== notification)
                            );
                        }, 5000);
                    }
                });

                client.subscribe('/topic/restauranteCadastrado', (message) => {
                    if (message.body) {
                        console.log('Received restauranteCadastrado message:', message.body);
                        const notification = { type: 'Restaurante Criado', content: JSON.parse(message.body) };
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

        return () => {
            client.deactivate();
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
