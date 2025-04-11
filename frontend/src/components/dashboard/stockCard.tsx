import React, { useState } from "react";
import NavBar from "../../common/navbar";
import OneMinuteChart from "./minuteChart";

interface StockData {
  topBuys: { price: number; notional: number }[];
  lowestSells: { price: number; notional: number }[];
  lastTenFulfilledOrders: { type: string; notionalAmount: number; id: string; price: number; symbol: string }[];
}

interface StockCardProps {
  stock: StockData;
  symbol: String;
}

const StockCard = ({ stock, symbol }: { stock: any; symbol: string }) => {
  const [showChart, setShowChart] = useState(false);
  const calculateNotional = (price: number, quantity: number) => price * quantity;

  return (
    <div onDoubleClick={() => setShowChart(!showChart)}>
      {showChart ? (
        <OneMinuteChart symbol={symbol} />
      ) : (
        <div className="stock-card">
          <div>{symbol}</div>
          <div className="price-lists">
            <div className="sell-prices">
              <h3>Top 5 Sell Prices</h3>
              <ul>
                {stock.lowestSells
                  ? stock.lowestSells.slice(0, 5).map((request: any, index: any) => (
                      <li key={index} className="sell-prices-list">
                        Price: {request.price.toLocaleString()} <br />
                        Notional: {request.notional.toLocaleString()}
                      </li>
                    ))
                  : ""}
              </ul>
            </div>
            <div className="buy-prices">
              <h3>Top 5 Buy Prices</h3>
              <ul>
                {stock.topBuys
                  ? stock.topBuys.slice(0, 5).map((request: any, index: any) => (
                      <li key={index} className="buy-prices-list">
                        Price: {request.price.toLocaleString()} <br />
                        Notional: {request.notional.toLocaleString()}
                      </li>
                    ))
                  : ""}
              </ul>
            </div>
          </div>
          <div className="row">
            <div className="col-12">
              <div className="table-responsive">
                <p>RO</p>
                <table className="table table-striped table-hover matched-trades">
                  <thead>
                    <tr>
                      <th scope="col">BUY/SELL</th>
                      <th scope="col">ID</th>
                      <th scope="col">Notional</th>
                      <th scope="col">Price</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stock.lastTenFulfilledOrders
                      ? stock.lastTenFulfilledOrders.map((matchedTrade: any, index: any) => (
                          <tr key={index}>
                            <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.type}</td>
                            <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.id.slice(-5)}</td>
                            <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.notionalAmount}</td>
                            <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.price}</td>
                          </tr>
                        ))
                      : ""}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StockCard;
