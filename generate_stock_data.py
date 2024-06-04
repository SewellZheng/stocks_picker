import requests
import yfinance as yf
import pandas as pd
import talib
from jinja2 import Environment, FileSystemLoader

def get_all_taiwan_stocks():
    url = 'https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL'
    response = requests.get(url)
    
    if response.status_code == 200:
        data = response.json()
        df = pd.DataFrame(data)
        
        # 提取股票代號
        stock_list = df['股票代號'].tolist()
        
        # 只保留有效的股票代號
        stock_list = [ticker + '.TW' for ticker in stock_list if ticker.isdigit()]
        
        return stock_list
    else:
        print("Error fetching data")
        return []

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
    
    for ticker in stock_list:
        df = get_stock_data(ticker)
        if df.empty:
            continue
        
        indicators = calculate_indicators(df)
        
        # Check conditions
        if (indicators['psy'].iloc[-1] > indicators['psy'].rolling(window=10).mean().iloc[-1]) and \
           (indicators['rsi'].iloc[-1] > indicators['rsi'].rolling(window=5).mean().iloc[-1]) and \
           (indicators['macd'].iloc[-1] - indicators['macd_signal'].iloc[-1] > -2.5):
            result.append(ticker)
    
    return result

def generate_html(stock_list):
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('template.html')
    html_content = template.render(stock_list=stock_list)
    
    with open('index.html', 'w', encoding='utf-8') as f:
        f.write(html_content)

def main():
    stock_list = get_all_taiwan_stocks()
    filtered_stocks = filter_stocks(stock_list)
    generate_html(filtered_stocks)

if __name__ == '__main__':
    main()
