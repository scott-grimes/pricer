class Order{
	
	String timestamp; 
	String order_id;
	String side;    //side is a "B" if this is a buy/bid order, or an "S" if this is a sell/ask order
	double price; 
	double size;
	
	//new orders are encoded as an array of strings:
	//timestamp "A" order_id side price size
	public Order(String[] parsed)
	{
		
	timestamp = parsed[0];
	order_id = parsed[2];
	side = parsed[3];
	price = Double.parseDouble(parsed[4]);
	size = Double.parseDouble(parsed[5]);
	
	}
	
	//d is a request for a number of shares to be taken from this order.
	//sharesAvail returns the returns the number of shares which can be
	//bought or sold. if d is less than size, d is returned. if d is greater
	//than size, the entire order (size) can be bought or sold
	public double sharesAvail(double d){
		if(d<size){
			return d;
		}
		
		return size;
	}
	
	//reduces the size of the order by *d
	public void reduce(double d){
		size-=d;
		if(size<0)
			size=0;
	}
	
	
}
