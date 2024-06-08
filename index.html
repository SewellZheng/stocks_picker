import requests
import yfinance as yf
import pandas as pd
import talib
from jinja2 import Environment, FileSystemLoader

def get_all_taiwan_stocks():
    url = 'https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL'
    response = requests.get(url)
    
    debug_log = []
    
    if response.status_code == 200:
        debug_log.append("Successfully fetched stock data from TWSE API.")
        data = response.json()
        df = pd.DataFrame(data)

        # Debug: Print the columns of the DataFrame
        # "df columnss: ['Code', 'Name', 'TradeVolume', 'TradeValue', 'OpeningPrice', 'HighestPrice', 'LowestPrice', 'ClosingPrice', 'Change', 'Transaction']"]
        debug_log.append(f"TWSE columns: {df.columns.tolist()}")
        
        # 提取股票代號
        stock_list = df['Code'].tolist()
        
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
    df = stock.history(period='3mo', interval='1d')
    return df

def calculate_indicators(df):
    #print(f'{df}')
    # 收盤價
    close = df['Close']
    close_yday = df['Close'].shift(1)

    # MACD
    macd, macdsignal, macdhist = talib.MACD(close, fastperiod=10, slowperiod=20, signalperiod=9)
    
    # KD (Stochastic Oscillator)
    kd_9k, kd_9d = talib.STOCH(df['High'], df['Low'], close, fastk_period=9, slowk_period=3, slowk_matype=0, slowd_period=3, slowd_matype=0)
    
    # PSY (Psychological Line)
    psy_10t = (close.pct_change().rolling(window=10).apply(lambda x: (x > 0).sum() / 10 * 100)).round(2)
    psy_20t = (close.pct_change().rolling(window=20).apply(lambda x: (x > 0).sum() / 20 * 100)).round(2)
    
    # RSI
    rsi_5t = talib.RSI(close, timeperiod=5).round(2)
    rsi_10t = talib.RSI(close, timeperiod=10).round(2)
    
    # Moving Averages
    ma_5 = talib.SMA(close, timeperiod=5)
    ma_10 = talib.SMA(close, timeperiod=10)
    ma_20 = talib.SMA(close, timeperiod=20)
    
    indicators = {
        'close_yday': close_yday,
        'close': close,
        'macd': macd,
        'macd_signal': macdsignal,
        'macd_hist': macdhist,
        'kd_9k': kd_9k,
        'kd_9d': kd_9d,
        'psy_10t': psy_10t,
        'psy_20t': psy_20t,
        'rsi_5t': rsi_5t,
        'rsi_10t': rsi_10t,
        'ma_5': ma_5,
        'ma_10': ma_10,
        'ma_20': ma_20
    }
    return indicators

def filter_stocks(stock_list):
    result = []
    debug_log = []
    for ticker in stock_list:
        #debug_log.append(f"Processing stock: {ticker}")
        df = get_stock_data(ticker)
        if df.empty:
            #debug_log.append(f"No data for stock: {ticker}")
            continue
        
        # 確保沒有 NaN 值影響比較
        #print(f'{df}')
        #df = df.dropna()
        #print(f'{df}')
        indicators = calculate_indicators(df)
        #print(f'{indicators}')

        #print(indicators['close'].iloc[-1])
        #print(indicators['close_yday'].iloc[-1])
        if (indicators['close'].iloc[-1] / indicators['close_yday'].iloc[-1] >= 0.5).item() and \
        (indicators['macd_hist'].iloc[-1] >= 1.5).item() and \
        (indicators['rsi_5t'].iloc[-1] > indicators['rsi_10t'].iloc[-1]).item() and \
        (indicators['psy_10t'].iloc[-1] >= indicators['psy_20t'].iloc[-1]-10).item() and \
        (indicators['kd_9k'].iloc[-1] >= indicators['kd_9d'].iloc[-1]).item() and \
        (abs(indicators['ma_5'].iloc[-1]-indicators['ma_10'].iloc[-1]) > 10).item() and \
        (abs(indicators['ma_5'].iloc[-1]-indicators['ma_20'].iloc[-1]) > 10).item():
            #debug_log.append(f"Stock {ticker} meets the conditions.")
            result.append(ticker)
        #else:
            #debug_log.append(f"Stock {ticker} does not meet the conditions.")
    return result, debug_log

def generate_html(stock_list, debug_log, html_table):
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('templates/template.html')
    html_content = template.render(stock_list=stock_list, debug_log=debug_log, html_table=html_table)
    with open('index.html', 'w', encoding='utf-8') as f:
        f.write(html_content)

def get_mothly_table(ticker):
    df = get_stock_data(ticker)
    return df.to_html()

def get_tech_table(ticker):
    df = get_stock_data(ticker)
    indicators = calculate_indicators(df)
    #print(indicators)
    indicators_df = pd.DataFrame.from_dict(indicators)
    return indicators_df.to_html(), ticker

def get_test_taiwan_stocks():
    stock_list = [
        "2308.TW",
        "3035.TW",
        "6719.TW"
        ]
    return stock_list, stock_list

def main():
    #stock_list, fetch_log = get_test_taiwan_stocks()
    stock_list, fetch_log = get_all_taiwan_stocks()
    debug_log = fetch_log

    filtered_stocks, filter_log = filter_stocks(stock_list)
    debug_log.append(filter_log)

    #generate_html(filtered_stocks, debug_log, '')

    html_table, tech_log = get_tech_table('3035.TW')
    debug_log.append(f'table is {tech_log}')
    generate_html(filtered_stocks, debug_log, html_table)

if __name__ == '__main__':
    main()