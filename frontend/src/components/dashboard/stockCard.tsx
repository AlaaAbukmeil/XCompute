import React from "react";
import NavBar from "../../common/navbar";

interface StockData {
  topBuys: { price: number; notional: number }[];
  lowestSells: { price: number; notional: number }[];
  lastTenFulfilledOrders: { type: string; notionalAmount: number; id: string; price: number; symbol: string }[];
}

interface StockCardProps {
  stock: StockData;
  symbol: String;
}

const StockCard: React.FC<StockCardProps> = ({ stock, symbol }) => {
  const calculateNotional = (price: number, quantity: number) => price * quantity;

  return (
    <div>
      <div className="stock-card">
        <h2>{symbol}</h2>
        <div className="price-lists">
          <div className="sell-prices">
            <h3>Top 5 Sell Prices</h3>
            <ul>
              {stock.lowestSells.slice(0, 5).map((request, index) => (
                <li className={`sell-prices-list`}>
                  Price: {request.price.toLocaleString()} <br />
                  Notional: {request.notional.toLocaleString()}
                </li>
              ))}
            </ul>
          </div>
          <div className="buy-prices">
            <h3>Top 5 Buy Prices</h3>
            <ul>
              {stock.topBuys.slice(0, 5).map((request, index) => (
                <li className={`buy-prices-list`}>
                  Price: {request.price.toLocaleString()} <br />
                  Notional: {request.notional.toLocaleString()}
                </li>
              ))}
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
                  {stock.lastTenFulfilledOrders.map((matchedTrade, index) => (
                    <tr key={index}>
                      <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.type}</td>
                      <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.id.slice(-5)}</td>
                      <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.notionalAmount}</td>
                      <td className={matchedTrade.type === "SELL" ? "text-danger" : "text-success"}>{matchedTrade.price}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StockCard;
