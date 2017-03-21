class Order:
    #order object
    #side is a "B" if this is a buy/bid order, 
    #or an "S" if this is a sell/ask order
    
    # new orders are encoded as an array of strings:
    # timestamp "A" order_id side price size
    def __init__(self, parsed):
        self.timestamp = parsed[0]
        self.order_id = parsed[2]
        self.side = parsed[3]
        self.price = float(parsed[4])
        self.size = float(parsed[5])
       
    # d is a request for a number of shares to be taken from this order.
    # sharesAvail returns the returns the number of shares which can be
    # bought or sold. if d is less than size, d is returned. if d is greater
    # than size, the entire order (size) can be bought or sold
    def sharesAvail(self, d):
        if(d<self.size):
            return d
        return self.size
    # reduces the size of the order by *d
    def reduce(self, d):
        self.size-=d
        if(self.size<=0.0):
            self.size=0.0
