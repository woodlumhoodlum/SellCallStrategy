package financeSim;

import java.util.concurrent.ThreadLocalRandom;
/**
 * 
 * Monte Carlo simulation of Sell Call strategy.
 * Strategy
 * 1. Pick a fairly stable stock which you would normally hold for a long time
 * 2. Each week pick a Sell Call which has a strike price unlikely to trigger
 * 3. If strike price isn't hit, collect premium and buy more stocks. Otherwise sell all shares at strike price and with the premium and proceeeds -> rebuy as much of the stock as you can
 * 4. Profit
 * 
 * Particular settings for this strategy:
 * 1. Contracts are weekly
 * 2. Strike Price is 5% higher than current share price, and the premium gain is .5% of the stock price (see XOM's options)
 * 
 * 
 * 
 * Shortcomings of the below code:
 * 1. contracts are normally 100 shares, and this code does not take this into account. Rather this code reinvests all partially shares possible.
 * 		a. Thus, this strategy would work w/ a lot of money but would need to be rewritten for normal amounts of money that you might try this with (a handful of contracts)
 * 		b. The rewritten version should have a supply of shares on the side which are not a part of contracts
 * 2. Stocks are not necessarily gaussian and simulated prices do not take into account real world factors such as macro-economic trends, momentum, patterns, etc.
 * 		a. To determine weekly stock price movement a gaussian number is sampled (-1 to 1) and avg annual return is added to it to skew prices to go slightly up overall.
 * 3. More research needs to be done into what a typical premium percentage might be on a weekly basis. Is .1% good, is .5% good?
 * 4. More research needs to be done on whether 5% above current price is a "safe" enough value for strike prices.
 * @author woodlum
 *
 */
public class SellCallStrategy {
	
	public static void main(String[] args) {
		int timeInWeeks = 52;
		double averageMarketReturn = .07; //(-1.00 to 2.07) / 52
		double weeklySellCallReturn = .005;
		int simulations = 1000;
		double returnsSum = 0;
		double priceSum = 0;
		double timesLosingMoney = 0;
		double timesUnderPerforming = 0;
		double outPerform10 = 0;
		double outPerform15 = 0;
		double outPerform25 = 0;
		double outPerform50 = 0;
		double outPerform75 = 0;
		double outPerform100 = 0;
		
		for(int i=0;i<simulations;i++) {
			double stockPrice = 100;
			double shares = 100;
			double initialValue = stockPrice*shares;
			double strikePrice = getOption(stockPrice);
			for(int week=0;week<timeInWeeks;week++) {
				//System.out.println(shares);
				double prevPrice = stockPrice;
				stockPrice += getIncrease(stockPrice, averageMarketReturn);
				shares = adjustSharesBasedOnOptionResult(prevPrice, stockPrice, strikePrice, shares, weeklySellCallReturn);
				strikePrice = getOption(stockPrice);
				
			}
			System.out.println("Shares: " + shares);
			System.out.println("Price: " + stockPrice);
			priceSum += stockPrice;
			System.out.println("Value: " + (stockPrice*shares));
			returnsSum += (stockPrice*shares)/initialValue;
			System.out.println("Return Ratio: " + returnsSum);
			if(stockPrice*shares < initialValue) {
				timesLosingMoney++;
			}
			if(stockPrice*shares < initialValue*(1+averageMarketReturn)) {
				timesUnderPerforming++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*1.10)) {
				outPerform10++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*1.15)) {
				outPerform15++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*1.25)) {
				outPerform25++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*1.55)) {
				outPerform50++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*1.75)) {
				outPerform75++;
			}
			if(stockPrice*shares > initialValue*((1+averageMarketReturn)*2)) {
				outPerform100++;
			}
		}
		
		System.out.println("----------------- F I N A L ------------------");
		System.out.println("Average Return Ratio: " +(returnsSum/simulations));
		System.out.println("Average Stock Price (should match average market return): " +(priceSum/simulations));
		System.out.println("Ratio of times Underperforming buy hold strategy: " +(timesUnderPerforming/simulations));
		System.out.println("Ratio of times Losing more money than you started with: " +(timesLosingMoney/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 10%: " +(outPerform10/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 15%: " +(outPerform15/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 25%: " +(outPerform25/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 50%: " +(outPerform50/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 75%: " +(outPerform75/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 100%: " +(outPerform100/simulations));
	}

	private static double adjustSharesBasedOnOptionResult(double prevPrice, double stockPrice, double strikePrice, double shares, double weeklySellCallReturn) {
		double premium = prevPrice * weeklySellCallReturn;
		if(strikePrice > stockPrice) {
			shares +=  (premium*(shares)/stockPrice);
		} else {
			double money = shares*strikePrice + (premium*(shares));
			shares = money/stockPrice;
			
		}
		return shares;
	}

	private static double getOption(double stockPrice) {
		System.out.println("stock price is now " + stockPrice);
		System.out.println("    get option: " + Math.ceil(stockPrice*1.05));
		return Math.ceil(stockPrice*1.05);
	}

	private static double getIncrease(double stockPrice, double averageMarketReturn) {
		// TODO verify gauss + avg is the correct formula
		double gauss = ThreadLocalRandom.current().nextGaussian(); // -1 to 1
		
				
		double result = (averageMarketReturn + gauss)/52; //add .07 to whatever gauss spits out and it should equate to a .07 return
		System.out.println("stock movement: " + result + " gauss: " + gauss);
		//System.out.println("new stock price: " + (stockPrice + stockPrice * result));
		return stockPrice * result;
	}

}
