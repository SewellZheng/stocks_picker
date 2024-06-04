import requests
import yfinance as yf
import pandas as pd
import talib
from jinja2 import Environment, FileSystemLoader
import os

def get_all_taiwan_stocks():
    url = 'https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL'
    response = requests.get(url)
    
    debug_log = []
    
    if response.status_code == 200:
        debug_log.append("Successfully fetched stock data from TWSE API.")
        data = response.json()
        df = pd.DataFrame(data)

        # Debug: Print the columns of the DataFrame
        debug_log.append(f"DataFrame columns: {df.columns.tolist()}")
        
        # 提取股票代號
        stock_list = df['證券代號'].tolist()
        
        # 只保留有效的股票代號
        stock_list = [ticker + '.TW' for ticker in stock_list if ticker.isdigit()]
        
        debug_log.append(f"Fetched {len(stock_list)} stocks.")
        return stock_list, debug_log
    else:
        error_message = "Error fetching data"
        debug_log.append(error_message)
        return [], debug_log

def get_stock_data(ticker):
    stock = yf.Ticker(ticker)
    df = stock.history(period='1mo', interval='1d')
    return df

def calculate_indicators(df):
    close = df['Close']
    
    # MACD
    macd, macdsignal, macdhist = talib.MACD(close, fastperiod=12, slowperiod=26, signalperiod=9)
    
    # KD (Stochastic Oscillator)
    slowk, slowd = talib.STOCH(df['High'], df['Low'], close, fastk_period=14, slowk_period=3, slowk_matype=0, slowd_period=3, slowd_matype=0)
    
    # PSY (Psychological Line)
    psy = close.pct_change().rolling(window=12).apply(lambda x: (x > 0).sum() / 12 * 100)
    
    # RSI
    rsi = talib.RSI(close, timeperiod=14)
    
    # Moving Averages
    ma_5 = talib.SMA(close, timeperiod=5)
    ma_10 = talib.SMA(close, timeperiod=10)
    ma_20 = talib.SMA(close, timeperiod=20)
    
    indicators = {
        'macd': macd,
        'macd_signal': macdsignal,
        'slowk': slowk,
        'slowd': slowd,
        'psy': psy,
        'rsi': rsi,
        'ma_5': ma_5,
        'ma_10': ma_10,
        'ma_20': ma_20
    }
    
    return indicators

def filter_stocks(stock_list):
    result = []
    debug_log = []
    
    for ticker in stock_list:
        debug_log.append(f"Processing stock: {ticker}")
        df = get_stock_data(ticker)
        if df.empty:
            debug_log.append(f"No data for stock: {ticker}")
            continue
        
        indicators = calculate_indicators(df)
        
        # Check conditions
        if (indicators['psy'].iloc[-1] > indicators['psy'].rolling(window=10).mean().iloc[-1]) and \
           (indicators['rsi'].iloc[-1] > indicators['rsi'].rolling(window=5).mean().iloc[-1]) and \
           (indicators['macd'].iloc[-1] - indicators['macd_signal'].iloc[-1] > -2.5):
            debug_log.append(f"Stock {ticker} meets the conditions.")
            result.append(ticker)
        else:
            debug_log.append(f"Stock {ticker} does not meet the conditions.")
    
    return result, debug_log

def generate_html(stock_list, debug_log):
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('template.html')
    html_content = template.render(stock_list=stock_list, debug_log=debug_log)
    
    with open('index.html', 'w', encoding='utf-8') as f:
        f.write(html_content)

def main():
    stock_list, fetch_log = get_all_taiwan_stocks()
    filtered_stocks, filter_log = filter_stocks(stock_list)
    debug_log = fetch_log + filter_log
    generate_html(filtered_stocks, debug_log)

if __name__ == '__main__':
    main()
