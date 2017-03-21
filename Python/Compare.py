class Compare:
   
    def __init__(self,fileName):
        self.f = open(fileName, 'r')
    
    def checkNext(self):   
        return str(self.f.readline())
    