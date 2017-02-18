
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f70fffb536fc47309b334629fa7bb28e)](https://www.codacy.com/app/pord911/AdvancedPing?utm_source=github.com&utm_medium=referral&utm_content=pord911/AdvancedPing&utm_campaign=badger)

TCPPing

This is a java ping application for testing device availability on the network. The application calculates the average and max RTT time between two nodes.

Application works in two modes. Cather mode, which receives packets and sends them back. And Pitcher mode which generates a defined number of packets per second, with a defined length. After the Pitcher receives the packets back from the Catcher it should generate a formated output every second printing the following values:

1. Time in HH:MM:SS
2. Total number of sent packets
3. Average RTT time
4. Max RTT time.
5. After 5 iterations print the number of sent and lost packets.

Usage

Position in the bin directory and run the following commands:

To run the application in the Catcher mode:

Windows:

java -cp .;../lib/* com.tcpping.main.TCPPing -c -bind 127.0.0.1 -port 9900

Linux:

java -cp .:../lib/* com.tcpping.main.TCPPing -c -bind 127.0.0.1 -port 9900

To run the application in the Pitcher mode:

Windows:

java -cp .;../lib/* com.tcpping.main.TCPPing -p -port 9900 -mps 100 -size 2000 localhost

Linux:

java -cp .:../lib/* com.tcpping.main.TCPPing -p -port 9900 -mps 100 -size 2000 localhost

Options

Catcher mode:

   -c    Run the application in the Catcher mode.

   -bind Bind the Catcher to an IP address on which it listens for messages.

   -port Port on which the Catcher listens for messages.


Pitcher mode:

   -p     Run the application in the Pitcher mode.

   -port  Port number.
 
   -mps   Messages per second.
 
   -size  Message size in characters, default is 300.
 
   <host> Hostname or IP address.
 

