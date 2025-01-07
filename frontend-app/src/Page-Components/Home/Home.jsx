import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';

function Home() {
    const [stompClient, setStompClient] = useState(null);
    const [connected, setConnected] = useState(false);
    const [greetings, setGreetings] = useState([]);
    const [name, setName] = useState('');

    useEffect(() => {
        const client = new Client({
            brokerURL: 'ws://localhost:8090/gs-guide-websocket',
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: (frame) => {
                setConnected(true);
                console.log('Connected: ' + frame);
                client.subscribe('/topic/greetings', (message) => {
                    const greeting = JSON.parse(message.body).content;
                    setGreetings((prevGreetings) => [...prevGreetings, greeting]);
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
            onWebSocketError: (error) => {
                console.error('Error with websocket', error);
                setConnected(false);
            },
            onDisconnect: () => {
                setConnected(false);
            }
        });

        setStompClient(client);

        return () => {
            if (client.active) {
                client.deactivate();
            }
        };
    }, []);

    const connect = () => {
        if (stompClient) {
            try {
                stompClient.activate();
            } catch (error) {
                console.error('Connection error:', error);
            }
        }
    };

    const disconnect = () => {
        if (stompClient) {
            stompClient.deactivate();
        }
        setConnected(false);
    };

    const sendName = () => {
        if (stompClient && stompClient.active) {
            stompClient.publish({
                destination: '/app/hello',
                body: JSON.stringify({ name }),
            });
        }
    };

    return (
        <div>
            <h1>WebSocket Chat</h1>
            <div>
                <button onClick={connect} disabled={connected}>
                    Connect
                </button>
                <button onClick={disconnect} disabled={!connected}>
                    Disconnect
                </button>
            </div>
            {connected && (
                <div>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Enter your name"
                    />
                    <button onClick={sendName}>Send</button>
                </div>
            )}
            <table>
                <thead>
                    <tr>
                        <th>Messages</th>
                    </tr>
                </thead>
                <tbody>
                    {greetings.map((message, index) => (
                        <tr key={index}>
                            <td>{message}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default Home;