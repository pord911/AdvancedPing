
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f70fffb536fc47309b334629fa7bb28e)](https://www.codacy.com/app/pord911/AdvancedPing?utm_source=github.com&utm_medium=referral&utm_content=pord911/AdvancedPing&utm_campaign=badger)

Java ping application

This is a java ping application for testing device availability on the network. The application calculates the average and max RTT time between two nodes. It also calculates the average timing for the packet to go in one direction and back (timing for A->B and B-A).

Application works in two modes. Cather mode, which receives packets, calculates A->B time difference and send the packet back. And Pitcher mode which generates a defined number of packets per second, with a defined length. After the Pitcher receives the packets back from the Catcher it should generate a formated output every second printing the following values:

1. Time in HH:MM:SS
2. Total number of sent packets
3. Average RTT time
4. Max RTT time.
5. Average A->B time.
6. Average B->A time.
7. After 5 iterations print the number of sent and lost packets.

Note:

When measuring the time difference between paths A->B and B->A there could be invalid values present due to different clocking setups between machines. It is required that both machines be synchronized with the same NTP server. But, even then there could be inalid values present, due to different clocking. 
