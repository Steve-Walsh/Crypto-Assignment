# Crypto-Assignment

A Project to demonstrate communication between Server and Client, via secure connection. 

Server creates a RSA key

The Cilent then makes a AES (Session Key)

Client then encrypts the session key using the servers public key

Server then decryptes the encrypted file using its private key copy of the session key

The session key is then used for any communication between the two.


To run just use java commuinication
