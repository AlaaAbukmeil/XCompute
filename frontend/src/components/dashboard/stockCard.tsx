import React from 'react';
import './StockCard.css';

interface StockData {
  symbol: string;
  sells: [number, number][];
  buys: [number, number][];
}

interface StockCardProps {
  stock: StockData;
}

const StockCard: React.FC<StockCardProps> = ({ stock }) => {
  const calculateNotional = (price: number, quantity: number) => price * quantity;

  return (
    <div className="stock-card">
      <h2>{stock.symbol}</h2>
      <div className="price-lists">
        <div className="sell-prices">
          <h3>Top 5 Sell Prices</h3>
          <ul>
            {stock.sells.slice(0, 5).map(([price, quantity], index) => (
              <li key={`sell-${index}`}>
                Price: ${price.toFixed(2)} | Quantity: {quantity} | 
                Notional: ${calculateNotional(price, quantity).toFixed(2)}
              </li>
            ))}
          </ul>
        </div>
        <div className="buy-prices">
          <h3>Top 5 Buy Prices</h3>
          <ul>
            {stock.buys.slice(0, 5).map(([price, quantity], index) => (
              <li key={`buy-${index}`}>
                Price: ${price.toFixed(2)} | Quantity: {quantity} | 
                Notional: ${calculateNotional(price, quantity).toFixed(2)}
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default StockCard;