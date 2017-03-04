TITLES:
This code was written by Jason Basanese
for Doc Po in ORG II at Eastern

RULES:
All variables must be strings(letter sequences).
All intermediate gates must be numbers.
Outputs may be numbers or letters.
If a gate only receives one input it will assume the other input to be a true value. Effectively making the gate a not.

USAGE:
To use this, edit the demo.txt or whatever you name your wiring file.
Once satisfied you may execute like:
java -jar wiring.jar demo.txt
Make sure the txt file is in the same folder as the jar file.
Sample output:
INPUTS                        OUTPUTS
a         b         c         OUT       
true      true      true      false     
false     true      true      true      
true      false     true      true      
false     false     true      true      
true      true      false     true      
false     true      false     true      
true      false     false     true      
false     false     false     true
