import java.util.*;
import java.text.DecimalFormat;
 
class Book{
	
	ArrayList<Order> bid;			//list of bid orders, sorted from high to low price
	ArrayList<Order> ask;			//list of ask orders, sorted from low to high price
	double target_size;				//target_size for our program
	double income = 0;      		//current income if we had purchased target_size shares
	double expense = 0;     		//current expense if we had sold target_size shares
	double ask_shares_avail = 0; 	//the number of shares availible in the ask list
	double bid_shares_avail = 0; 	//the number of shares availible in the bid list
	String lastTimeStamp;			//the time stamp of the last order from our log file in stdin
	DecimalFormat df = new DecimalFormat(".00"); //formats our print to use two decimal places
	
	public Book(double t){
		bid = new ArrayList<Order>(); 
		ask = new ArrayList<Order>(); 
		target_size = t;
	
	}
	
	//String[] line is formatted for a reduce order as follows:
	//timestamp "R" order_id size
	public void reduceOrder(String[] line){
	
	    lastTimeStamp = line[0];
		String order_id = line[2];
		double size = Double.parseDouble(line[3]);
		
		String orderToReduce = getOrder_byId(order_id); 	//returns a string "B index" for a bid order, "A index" for an ask order,
															//where index is the index of the order in either the bid or ask list
		
		int index = Integer.parseInt(orderToReduce.split(" ")[1]);
		
		if(orderToReduce.charAt(0)=='B') 				//if the first character of our string is a 'B', the order is a bid,
		{												//otherwise the character is an 'A', and the order is an ask.
			bid.get(index).reduce(size); 				//reduces the size of our bid order by "size"
			if(bid.get(index).size==0)   				//if the size of the bid order has been reduced to 0, removes the bid from the bid list
			{
			bid.remove(index);
			}
			bid_shares_avail-=size;   					//decreases the number of availible shares in our bid list
			getIncome();								//computes our new income
			}
		else 											//since the first character was not a 'B', the order is an ask order.
		{
			ask.get(index).reduce(size);				//reduces the size of our ask order
			if(ask.get(index).size==0)					//if the size of the order has been reduced to 0, remove the order from the ask list
			{											
				ask.remove(index);
			}
			ask_shares_avail-=size;						//decreases the number of availible shares in our bid list
			getExpenses();								//computes our new expenses
		}
	}
	
	//returns a string indicating the location of the order with the identifier "order_id"
	//if the order is an ask, returns "A index"
	//if the order is a bid, returns  "B index"
	public String getOrder_byId(String order_id){
		String answer = "";
		
		for(int i = 0;i<bid.size();i++)						//searches for an order in the bid list with *order_id
		{
			if(bid.get(i).order_id.equals(order_id))
			{
				answer+="B "+String.valueOf(i);
				return answer;
			}
				
		}
		
		for(int i = 0;i<ask.size();i++)						//searches for an order in the bid list with *order_id
		{													
			if(ask.get(i).order_id.equals(order_id))
			{
				answer+="A "+String.valueOf(i);
				return answer;
			}
		}
		
		return answer;										//returns an empty string if no order was found
	}	
	
	//inserts an order into the bid list in the correct position
	// the bid list is sorted from high to low price
	public void newBid(Order temp){
		boolean added = false;
		
		for(int i = 0;i<bid.size();i++)
			{
				if(bid.get(i).price<temp.price)
				{
					bid.add(i,temp);
					added = true;
					break;
				}
			}
			
		if(!added) 						//if no location inside the list was found, the order is placed at the end of the list
		{					
		bid.add(temp);
		}
		bid_shares_avail+=temp.size;	//increases the number of availible bid shares to account for our new order
		getIncome();					//computes our new income
		}
	
	//inserts an order into the ask list in the correct position
	// the ask list is sorted from low to high price
	public void newAsk(Order temp){
		boolean added = false;
		
		for(int i = 0;i<ask.size();i++)
		{
		if(ask.get(i).price>temp.price)
		{
			ask.add(i,temp);
			added = true;
			break;
		}
		}
			
		if(!added){						//if no location inside the list was found, the order is placed at the end of the list
		ask.add(temp);
		}
		
		ask_shares_avail+=temp.size;	//increases the number of availible ask shares to account for our new order
		getExpenses();					//computes our new expenses
		
	}
	
	//new order commands are formatted as follows:
	//timestamp "A" order_id side price size
	//"A" indicates that the order is a new order to be added
	//side is either a "B" for a buy order, or an "S" for a sell order
	public void addOrder(String[] line){
		
		Order temp = new Order(line);   //creates our new order
		lastTimeStamp = line[0];        //updates the current time stamp
		
		if(temp.side.equals("B"))
		{
			newBid(temp);				//adds our order to the bid list
		}
		else
		{
			newAsk(temp);				//adds our order to the ask list
		}
	}

	//computes our current income
	//by filling our order for target-size shares highest price first
	public void getIncome(){
	
			double newIncome = 0;
			if(bid_shares_avail>=target_size){
				
			double leftToSell = target_size;
			
			int index = 0;
			while(leftToSell>0)
			{
				double shares = bid.get(index).sharesAvail(leftToSell);
				newIncome+=shares*bid.get(index).price;
				leftToSell-=shares;
				index++;
			}
			}
		
		if(income!=newIncome){											//only print if there is a change to our income
		
			income = newIncome;
			if(bid_shares_avail>=target_size){
				Pricer.write(lastTimeStamp+" S "+df.format(income));	//will print our income total, we are able to buy shares
			}
			else{
				Pricer.write(lastTimeStamp+" S NA");					//will print that income is NA, there are not enough availible shares
			}
		}
	}
	
	//computes our current expenses
	//by filling our order for target-size shares lowest price first
	public void getExpenses()
	{		double newExpense = 0;
			if(ask_shares_avail>=target_size){
			
			double leftToBuy = target_size;
			int index = 0;
			while(leftToBuy>0)
			{
				double shares = ask.get(index).sharesAvail(leftToBuy);
				newExpense+=shares*ask.get(index).price;
				leftToBuy-=shares;
				index++;
			}
			}
		if(expense!=newExpense){				//only print to stdout if there is a change to our expenses
			expense = newExpense;
			if(ask_shares_avail>=target_size){	
				Pricer.write(lastTimeStamp+" B "+df.format(expense));		//will print our expense total, we are able to sell shares
			}
			else{
				Pricer.write(lastTimeStamp+" B NA");						//will print that expenses are NA, there are not enough availible shares
			}
		}
	}
	
	
	
	
	
}