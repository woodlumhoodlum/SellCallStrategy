package financeSim;

import java.util.concurrent.ThreadLocalRandom;

public class SellCallStrategy {
	
	public static void main(String[] args) {
		
		
		
		int timeInWeeks = 52;
		double averageMarketReturn = .07; //(-1.00 to 2.07) / 52
		double weeklySellCallReturn = .05;
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
				shares = adjustSharesBasedOnOptionResult(prevPrice, stockPrice, strikePrice, shares);
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
		
		System.out.println("Average Return: " +(returnsSum/simulations));
		System.out.println("Average Stock Price: " +(priceSum/simulations));
		System.out.println("Ratio of times Underperforming buy hold strategy: " +(timesUnderPerforming/simulations));
		System.out.println("Ratio of times Losing more money than you started with: " +(timesLosingMoney/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 10%: " +(outPerform10/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 15%: " +(outPerform15/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 25%: " +(outPerform25/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 50%: " +(outPerform50/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 75%: " +(outPerform75/simulations));
		System.out.println("Ratio of times Outperforming buy hold by 100%: " +(outPerform100/simulations));
	}

	private static double adjustSharesBasedOnOptionResult(double prevPrice, double stockPrice, double strikePrice, double shares) {
		double premium = prevPrice * .005;
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
