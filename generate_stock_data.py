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
    #print(df)
    # 收盤價
    close = df['Close']
    
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
        
        indicators = calculate_indicators(df)

        # 確保沒有 NaN 值影響比較
        #indicators = indicators.dropna()

        # Check conditions
        # 取出 PSY 指標序列中的最後一個值，即最新的 PSY 值。 > 計算 PSY 指標在最近 10 天的滾動平均值，並取出平均值最後一個值，即最新的 10 日均值。
        # 取出 RSI 指標序列中的最後一個值，即最新的 RSI 值。 > 計算 RSI 指標在最近 5 天的滾動平均值，並取出平均值最後一個值，即最新的 5 日均值。
        '''
        PSY (Psychological Line) 指標的詳細解釋
        PSY 指標是一種技術分析工具，用來衡量一定時間段內價格上漲天數的百分比。它的計算方法如下：
        psy = close.pct_change().rolling(window=10).apply(lambda x: (x > 0).sum() / 10 * 100)
        價格變動百分比 (pct_change)
        close.pct_change() 計算每日收盤價的變動百分比。
        滾動窗口 (rolling)
        .rolling(window=10) 創建一個 10 天的滾動窗口。
        應用函數 (apply)
        .apply(lambda x: (x > 0).sum() / 10 * 100)：對每個滾動窗口內的數據應用一個 lambda 函數。
        x > 0 計算在這 10 天內價格上漲的天數。
        (x > 0).sum() 計算價格上漲天數的總和。
        (x > 0).sum() / 10 * 100 將價格上漲天數轉換為百分比，即 PSY 指標。

        計算每個 10 天窗口內價格上漲的百分比，並生成 PSY 指標序列。
        應用 PSY 指標的條件：
        if (indicators['psy'].iloc[-1] > indicators['psy'].rolling(window=20).mean().iloc[-1])

        這段代碼的意思是：
        indicators['psy'].iloc[-1]

        取出 PSY 指標序列的最後一個值，即最新的 PSY 值。
        indicators['psy'].rolling(window=10).mean().iloc[-1]

        計算 PSY 指標的 10 天滾動平均值，並取出這個平均值的最後一個值，即最新的 10 日均值。
        檢查最新的 PSY 值是否大於其最近 20 天的平均值。
        詳細解釋這個條件的意義：
        PSY 指標的最新值大於其 20 天均值
        表示市場近期內（10 天內）價格上漲的天數比例大於過去 20 天內的平均上漲天數比例。
        這通常被解讀為市場短期內有上升動能，可能會繼續上漲。
        總結
        PSY 指標：衡量一定時間段內（如 10 天）價格上漲天數的百分比。
        條件檢查：最新的 PSY 值是否大於其最近 20 天的平均值，以判斷市場的短期上升動能。
        這個條件可以幫助交易者判斷市場是否有強烈的上升趨勢，並可能在這種情況下考慮買入。與 MACD 類似，這是一種用來識別市場趨勢的技術指標。
        ---------------------------------------------
        RSI (Relative Strength Index) 相對強弱指數的詳細解釋
        RSI 指標是一種常用的技術分析工具，用來衡量價格變動的速度和變動的幅度，以判斷市場的超買或超賣情況。RSI 的計算公式如下：
        rsi = talib.RSI(close, timeperiod=5)
        RSI 的計算步驟：
        收盤價格 (close)
        使用收盤價序列進行計算。
        時間周期 (timeperiod=5)
        設定 RSI 的計算周期為 5 天。
        talib.RSI 函數使用 5 天的價格數據來計算 RSI 值。
        RSI 的意義：
        RSI 值範圍：0 到 100
        超買區域：RSI > 70，表示市場可能過度上漲，有回調的風險。
        超賣區域：RSI < 30，表示市場可能過度下跌，有反彈的機會。
        應用 RSI 指標的條件：
        if (indicators['rsi'].iloc[-1] > indicators['rsi'].rolling(window=10).mean().iloc[-1])
        這段代碼的意思是：
        indicators['rsi'].iloc[-1]
        取出 RSI 指標序列的最後一個值，即最新的 RSI 值。
        indicators['rsi'].rolling(window=10).mean().iloc[-1]
        計算 RSI 指標的 10 天滾動平均值，並取出這個平均值的最後一個值，即最新的 10 日均值。
        檢查最新的 RSI 值是否大於其最近 10 天的平均值。
        詳細解釋這個條件的意義：
        RSI 指標的最新值大於其 10 天均值
        表示市場近期內（5 天內）價格變動的相對強度大於過去 10 天內的平均強度。
        這通常被解讀為市場短期內有上升動能，可能會繼續上漲。
        總結
        RSI 指標：衡量價格變動的速度和幅度，以判斷市場的超買或超賣情況。
        條件檢查：最新的 RSI 值是否大於其最近 10 天的平均值，以判斷市場的短期上升動能。
        這個條件可以幫助交易者判斷市場是否有強烈的上升趨勢，並可能在這種情況下考慮買入。與 PSY 類似，這是一種用來識別市場趨勢的技術指標。使用 RSI 指標可以更好地了解市場的相對強弱，從而做出更明智的交易決策。
        -----------------------------------------------------------------------------------------
        1. macd
        這是 MACD 線，它是快速指數移動平均線 (EMA) 和慢速指數移動平均線之間的差異。具體來說，計算過程如下：

        快速 EMA (12 日)
        慢速 EMA (26 日)
        MACD 線：MACD = 快速 EMA - 慢速 EMA
        這條線反映了兩條不同周期的 EMA 之間的差距，並用來衡量短期價格變動的強度。

        2. macdsignal
        這是 MACD 信號線，也稱為觸發線，它是 MACD 線的 9 日指數移動平均線。具體計算如下：

        信號線 (9 日 EMA)：計算 MACD 線的 9 日指數移動平均線。
        這條線用來產生買賣信號，當 MACD 線與信號線交叉時，會產生交易信號。例如：

        當 MACD 線由下向上穿越信號線時，通常被視為買入信號。
        當 MACD 線由上向下穿越信號線時，通常被視為賣出信號。
        3. macdhist
        這是 MACD 柱狀圖 (Histogram)，它是 MACD 線和信號線之間的差異。具體計算如下：

        柱狀圖：MACD 柱狀圖 = MACD 線 - 信號線
        柱狀圖提供了 MACD 線與信號線之間距離的可視化表示。當柱狀圖為正且擴大時，表示上升動能增強；當柱狀圖為負且擴大時，表示下降動能增強。
        '''
        #print(indicators['rsi_5t'].iloc[-1])
        #print(indicators['rsi_10t'].iloc[-1])
        if (indicators['psy_10t'].iloc[-1] > indicators['psy_20t'].iloc[-1]).item() and \
           (indicators['rsi_5t'].iloc[-1] > indicators['rsi_10t'].iloc[-1]).item() and \
           (indicators['kd_9k'].iloc[-1] > indicators['kd_9d'].iloc[-1]).item() and \
           (indicators['macd_hist'].iloc[-1] > -0.8).item():
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
    stock_list, fetch_log = get_test_taiwan_stocks()
    #stock_list, fetch_log = get_all_taiwan_stocks()
    debug_log = fetch_log

    filtered_stocks, filter_log = filter_stocks(stock_list)
    debug_log.append(filter_log)

    #generate_html(filtered_stocks, debug_log, '')

    html_table, tech_log = get_tech_table('3035.TW')
    debug_log.append(f'table is {tech_log}')
    generate_html(filtered_stocks, debug_log, html_table)

if __name__ == '__main__':
    main()