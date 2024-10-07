import React, { useState, useEffect, useCallback } from "react";
import StockCard from "./stockCard";
import NavBar from "../../common/navbar";

// Define the structure of the OrderBookSummary
interface OrderBookSummary {
  topBuys: { price: number; notional: number }[];
  lowestSells: { price: number; notional: number }[];
  lastTenFulfilledOrders: {
    type: string;
    notionalAmount: number;
    originalNotionalAmount: number;
    id: string;
    price: number;
    symbol: string;
  }[];
}

// Define the structure of the messages state
interface Messages {
  [symbol: string]: OrderBookSummary;
}

const useWebSocket = (url: string) => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [messages, setMessages] = useState<Messages>({});

  useEffect(() => {
    const ws = new WebSocket(url);

    ws.onopen = () => {
      console.log('WebSocket connected');
    };

    ws.onmessage = (event: MessageEvent) => {
      try {
        const data = JSON.parse(event.data) as Messages;
        setMessages((prevMessages) => ({
          ...prevMessages,
          ...data
        }));
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    };

    ws.onerror = (error: Event) => {
      console.error('WebSocket error:', error);
    };

    ws.onclose = () => {
      console.log('WebSocket disconnected');
    };

    setSocket(ws);

    return () => {
      ws.close();
    };
  }, [url]);

  const sendMessage = useCallback((message: any) => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify(message));
    }
  }, [socket]);

  return { socket, messages, sendMessage };
};

const Dashboard: React.FC = () => {
  const { messages, sendMessage } = useWebSocket('ws://localhost:8081/api/websocket/orderbook');
  console.log({messages});

  return (
    <div>
      <NavBar />
      <div className="dashboard">
        {Object.entries(messages).map(([symbol, stock]) => (
          <StockCard key={symbol} symbol={symbol} stock={stock} />
        ))}
      </div>
    </div>
  );
};

export default Dashboard;