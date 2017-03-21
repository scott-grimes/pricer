# pricer

Pricer analyzes a log file, which consists of buy and sell orders placed
in a stock market. Pricer takes two command-line arguments. The first is target-size,
which tracks the income or expense which would be incurred if "target-size" shares were bought
or sold. The second is an optional filename to check Pricers output against a known good
output log file. Pricer reads the log file from std.in, and writes to std.out.

See the "Order Book Programming Problem" pdf file for more info

Example:
First Unzip pricer.in.gz

Then call the program as follows in java or python:

Java:

java Pricer 10000 pricer.out.10000 <pricer.in >myOut.txt

or

java Pricer 10000 <pricer.in >myOut.txt

Python:

python Pricer.py 10000 pricer.out.10000 <pricer.in >myOut.txt

or

python Pricer.py 10000 <pricer.in >myOut.txt

A sample input/output set of documents is also availible which is easy to follow
