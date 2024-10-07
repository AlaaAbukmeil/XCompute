import React, { useState, useEffect } from "react";
import axios from "axios";
import StockCard from "./stockCard";
import { baseUrl, getAxiosRequestOptions } from "../../common/cookie";
import NavBar from "../../common/navbar";

interface StockData {
  topBuys: { price: number; notional: number }[];
  lowestSells: { price: number; notional: number }[];
  lastTenFulfilledOrders: {type:string,notionalAmount:number, originalNotionalAmount:number,id:string, price:number,symbol:string}[];

}

const Dashboard: React.FC = () => {
  const [stocks, setStocks] = useState<StockData[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Replace with your actual API endpoint
        const response = await axios.get(baseUrl + "books", getAxiosRequestOptions);
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
    <div>
      <NavBar />

      <div className="dashboard">
        {Object.keys(stocks).map((symbol: any) => (
          <StockCard key={symbol} symbol={symbol} stock={stocks[symbol]} />
        ))}
      </div>
    </div>
  );
};

export default Dashboard;
