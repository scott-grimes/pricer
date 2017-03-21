import sys
from Order import Order
from Compare import Compare
class Book:
    
    #first arg is target-size
    #second arg is an optional comparison file
    def __init__(self, t, compareFile = "none"):
        self.bid = [] #list of bid orders, sorted from high to low
        self.ask = [] #list of ask orders, sorted from low to high
        self.target_size = float(t) #target_size for our program
        self.ask_shares_avail = 0.0 #number of shares avail in the ask list
        self.bid_shares_avail = 0.0 #number of shares avail in the bid list
        self.income = 0.0 #current income if we had purchased shares
        self.expense=0.0 #current expense if we had sold shares
        
        #if there is a comparison file, turn on debug mode
        #and check our outputs against the comparison file
        if(compareFile is not "none"):
            self.compare = Compare(compareFile)
            self.DEBUG = True
        else:
            self.DEBUG = False
        
    #each line of our input log is split and parsed as follows:
    #Add to order book: timestamp "A" order_id side price size
    #Reduce order:      timestamp "R" order_id size
    #"A" and "R" designate either an "Add" order or "Reduce" order
    # order_id is a unique string identifying the order in our book
    # side is either "B" for a Bid or "A" for an Ask order
    def processLog(self,line):
        
        #self.inputLine is used for debugging purposes to print the input line
        self.inputLine = line
        
        parsed = line.split(" ")
        #if parsed[1] is "R" we have a reduce order
        #otherwise we have an add order
        if(parsed[1] is "R"):
            self.reduceOrder(parsed)
        else:
            self.addOrder(parsed)
        
    # line is a string array formatted for a reduce order as follows:
    #[timestamp "R" order_id size]
    def reduceOrder(self, line):
        self.lastTimeStamp = line[0]
        order_id = line[2]
        size = float(line[3])
        
        #returns a string "B index" for a bid order, "A index" for an ask order,
        #where index is the index of the order in either the bid or ask list
        orderToReduce = self.getOrder_byId(order_id)
        
        #if orderToReduce returned a blank string, the order does not exist
        if(orderToReduce is ""):
            return
        
        #if the first character of our string is a 'B', the order is a bid,
        #otherwise the character is an 'A', and the order is an ask.
        #reduces the size of our bid order by "size"
        #if the size of the bid order has been reduced to 0, removes the bid from the bid list
        index = int(orderToReduce.split(" ")[1])
        if(orderToReduce.split(" ")[0] == "B"):
            #we have a bid order
            
            self.bid[index].reduce(size)
            if(self.bid[index].size <.001 ): #if the size of our order is zero, remove the order
                del self.bid[index]
            #decreases the number of availible bid shares
            self.bid_shares_avail-=size
            #calculates income
            self.getIncome()
        else:
            #we have an ask order
            
            self.ask[index].reduce(size)
            if(self.ask[index].size <.001 ): #if the size of our order is zero, remove the order
                del self.ask[index]
            #decreases the number of availible bid shares
            
            self.ask_shares_avail-=size
            self.getExpenses()
    
    #returns a string indicating the location of the order with the identifier "order_id"
    #if the order is an ask, returns "A index"
    #if the order is a bid, returns  "B index"
    def getOrder_byId(self, order_id):
        index = -1
        #searches for the order in the bid list
        #which has the specified order_id
        for index, item in enumerate(self.bid):
            if item.order_id == order_id:
                return "B "+str(index)
            
        #searches for the order in the ask list
        #which has the specified order_id
        for index, item in enumerate(self.ask):
            if item.order_id == order_id:
                return "A "+str(index)
            
        #returns an empty string if no order with order_id was found in the 
        #bid or ask lists
        return "" 
    
    #inserts an order into the bid list in the correct position
    # the bid list is sorted from high to low price
    def newBid(self, temp):
        added = False
        
        for index, item in enumerate(self.bid):
            if(item.price<temp.price):
                self.bid.insert(index,temp)
                added = True
                break
        #if no location was found, appends the order to the end of our list
        if(not added): 
            self.bid.append(temp)
        #increases the number of availible shares
        self.bid_shares_avail += temp.size
        #computes our new income
        self.getIncome()
        
    #inserts an order into the ask list in the correct position
    # the ask list is sorted from low to high price
    def newAsk(self, temp):
        added = False
        
        
        for index, item in enumerate(self.ask):
            if(item.price>temp.price):
                self.ask.insert(index,temp)
                added = True
                break
        #if no location was found, append to the end of our list    
        
        
                
        if(not added):
            self.ask.append(temp)
        
        #increases the size of our availilbe shares
        self.ask_shares_avail += temp.size
        #recalculate expenses
        self.getExpenses()
    
    #new order commands are formatted as follows:
    #timestamp "A" order_id side price size
    #"A" indicates that the order is a new order to be added
    #side is either a "B" for a buy order, or an "S" for a sell order
    def addOrder(self, line):
        #creates a new order
        temp = Order(line) 
        
        #updates current timestamp
        self.lastTimeStamp = str(line[0])
        
        if(temp.side is "B"):
            #adds order to bid list
            self.newBid(temp)
        else:
            #adds order to ask list
            self.newAsk(temp)
            
            
    #computes our current income
    #by filling our order for target-size shares highest price first       
    def getIncome(self):
        newIncome = 0.0
        
        if(self.bid_shares_avail>=self.target_size):
            leftToSell = self.target_size
            index = 0
            while(leftToSell>0.001):
                shares = self.bid[index].sharesAvail(leftToSell)
                newIncome += shares*self.bid[index].price
                leftToSell-=shares
                index+=1
                
       #only print if there is a change to our income
        if(self.notEqual(self.income , newIncome)):
            self.income = newIncome
            if(self.bid_shares_avail>=self.target_size):
                #will print our income total, we are able to buy shares
                self.write(str(self.lastTimeStamp)+" S "+ str("%.2f" % self.income))
            else:
                #will print that income is NA, there are not enough availible shares
                self.write(str(self.lastTimeStamp)+" S NA")
    
    #computes our current expenses
    #by filling our order for target-size shares lowest price first        
    def getExpenses(self):
        newExpense = 0.0
        
        if(self.ask_shares_avail>=self.target_size):
            leftToBuy = self.target_size
            index = 0
            while(leftToBuy>0.001):
                shares = self.ask[index].sharesAvail(leftToBuy)
                newExpense+=shares*self.ask[index].price
                leftToBuy-=shares
                index+=1
        #only print to stdout if there is a change to our expenses
        if(self.notEqual(self.expense,newExpense)):
            self.expense = newExpense
            if(self.ask_shares_avail>=self.target_size):
                #will print our expense total, we are able to sell shares
                self.write(str(self.lastTimeStamp)+" B "+str("%.2f" % self.expense))
            else:
                #will print that expenses are NA, there are not enough availible shares
                self.write(str(self.lastTimeStamp)+" B NA")
    
    def write(self,line):
        
        print line
    
        ##debugger! checks our output against a comparison file. if any 
        # discrepancy is noted, the program is halted and the outputs 
        #are compared
        if(self.DEBUG):
            compareLine = self.compare.checkNext()
           
            if(line != compareLine.strip()):
                print ""
                print "Compare Line: "+compareLine
                print "My Line     : "+line
                print "Input Line  : "+self.inputLine
                sys.exit()
    
    #returns true if two floats are not equal to each other
    def notEqual(self,a,b):
        eps = .0001
        return (abs(a-b)>eps)