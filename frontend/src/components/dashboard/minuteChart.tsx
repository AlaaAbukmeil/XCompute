import React, { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import { useWebSocket } from "./prices";

interface DashboardProps {
  symbol: string;
}

const OneMinuteChart: React.FC<DashboardProps> = ({ symbol }) => {
  const { messages } = useWebSocket("ws://localhost:8081/api/websocket/minute-price-charts");
  const [series, setSeries] = useState<any[]>([]);
  const [options, setOptions] = useState<any>({});

  useEffect(() => {
    if (!messages[symbol]) return;

    const data: any = messages[symbol];
    const timestamps = Object.keys(data)

    const candleData = timestamps.map((t, index) => {
      const current = data[t];
      const prev = index > 0 ? data[timestamps[index - 1]] : current;

      return {
        x: new Date(t),
        y: [
          prev.max, // open (previous close)
          current.max, // high
          current.min, // low
          current.max, // close
        ],
      };
    });
    console.log({ candleData });

    setSeries([
      {
        name: symbol,
        data: candleData,
      },
    ]);

    setOptions({
      chart: {
        type: "candlestick",
        height: 400,
        animations: {
          enabled: false,
        },
        toolbar: {
          show: false,
        },
      },
      title: {
        text: symbol,
        align: "center",
        style: {
          fontSize: "16px",
          fontWeight: "bold",
        },
      },
      xaxis: {
        type: "datetime",
        labels: {
          datetimeUTC: false,
          format: "HH:mm",
        },
        axisBorder: {
          show: true,
        },
        axisTicks: {
          show: true,
        },
      },
      yaxis: {
        tooltip: {
          enabled: true,
        },
        labels: {
          formatter: (value: number) => value.toFixed(2),
        },
        floating: false,
        position: "right",
      },
      grid: {
        show: true,
        borderColor: "#f0f0f0",
        strokeDashArray: 0,
      },
      plotOptions: {
        candlestick: {
          colors: {
            upward: "#26a69a",
            downward: "#ef5350",
          },
          wick: {
            useFillColor: true,
          },
        },
      },
      tooltip: {
        enabled: true,
        theme: "light",
        x: {
          format: "HH:mm",
        },
        y: {
          formatter: (value: number) => value.toFixed(2),
        },
        custom: ({ seriesIndex, dataPointIndex, w }: any) => {
          const o = w.globals.seriesCandleO[seriesIndex][dataPointIndex];
          const h = w.globals.seriesCandleH[seriesIndex][dataPointIndex];
          const l = w.globals.seriesCandleL[seriesIndex][dataPointIndex];
          const c = w.globals.seriesCandleC[seriesIndex][dataPointIndex];

          return `
            <div class="apexcharts-tooltip-box">
              <div>Open: ${o.toFixed(2)}</div>
              <div>High: ${h.toFixed(2)}</div>
              <div>Low: ${l.toFixed(2)}</div>
              <div>Close: ${c.toFixed(2)}</div>
            </div>
          `;
        },
      },
    });
  }, [messages, symbol]);

  return (
    <div className="chart-container" style={{ padding: "20px" }}>
      <div
        style={{
          height: "400px",
          background: "white",
          borderRadius: "8px",
          padding: "16px",
          boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
        }}
      >
        {series.length > 0 && <ReactApexChart options={options} series={series} type="candlestick" height={368} />}
      </div>
    </div>
  );
};

export default OneMinuteChart;
