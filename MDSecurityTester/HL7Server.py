import socket
import logging
import datetime
import argparse



def startServer(port,message):

    try:
        serverSocket = socket.socket()
        serverSocket.bind(('',port))

        serverSocket.listen(100)

        print "Server running on the port "+ str(port)

        while True:

            connection, address = serverSocket.accept()

            print "Got a connection from host at: ", str(address)
            print message

            connection.send(message)

            connection.close()

    except KeyboardInterrupt:
        connection.close()




if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 server')
    parser.add_argument('port', type=int, help='Enter the port number on which the server should be running')
    parser.add_argument('message', help='enter timeout response in seconds')

    args = parser.parse_args()

    port = int(args.port)
    message = str(args.message)

    print port

    if float(args.port) != 0 or not args.port:
        startServer(port,message)

    else:
        print "Enter port number"


