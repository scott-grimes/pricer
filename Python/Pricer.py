import sys
from Book import Book

#argv0 is our file name, arg1 is target_size
target_size = sys.argv[1] 

#if the optional second argv is given
#use it as a comparison file to see if our program
#outputs are correct

if(len(sys.argv)>2): 
    book = Book(target_size,sys.argv[2])
else:
    book = Book(target_size)

#looks at each line in stdin and proccesses it 
for line in sys.stdin:
    book.processLog(line)

sys.exit(0)

        

        
