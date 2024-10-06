import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Dashboard.css";
import StockCard from "./stockCard";
import { baseUrl } from "../../common/cookie";

interface StockData {
  symbol: string;
  sells: [number, number][];
  buys: [number, number][];
}

const Dashboard: React.FC = () => {
  const [stocks, setStocks] = useState<StockData[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Replace with your actual API endpoint
        const response = await axios.get(baseUrl + "books");
        setStocks(response.data);
      } catch (error) {
        console.error("Error fetching stock data:", error);
      }
    };

    fetchData();
    const interval = setInterval(fetchData, 5000); // Refresh every 5 seconds

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="dashboard">
      {stocks.map((stock) => (
        <StockCard key={stock.symbol} stock={stock} />
      ))}
    </div>
  );
};

export default Dashboard;
