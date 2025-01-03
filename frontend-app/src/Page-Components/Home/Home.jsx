/* eslint-disable react/no-unescaped-entities */
import { useEffect } from "react";
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { Client } from "@stomp/stompjs";

function Home() {
  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8090/ws", // WebSocket server URL
      reconnectDelay: 5000, // Reconnect after 5 seconds if disconnected
      debug: (str) => console.log(str), // Debug logs
    });

    client.onConnect = () => {
      console.log("Connected");

      // Subscribe to the topic
      client.subscribe("/topic/message", (message) => {
        if (message.body) {
          console.log("message.body",message.body)
        }
      });
    };

    client.onStompError = (frame) => {
      console.error("Broker error:", frame.headers["message"]);
      console.error("Additional details:", frame.body);
    };

    client.activate(); // Connect to the WebSocket server

    return () => {
      client.deactivate(); // Cleanup the connection when the component unmounts
    };
  }, []);
    
  return (
    <>
      <h1>HOME</h1>
    </>
  )
}

export default Home;
