#HL7 2.x Messenger script, anirudh duggal

import socket
import argparse
import time
import logging

LOG_FILENAME = str(time.strftime("%Y%m%d-%H%M%S")) + "-HL7Messenger" + ".log"

def sendMessage(host,port,message):

    #timeout for the reply, if the reply is not received in 2 sec, close connection
    timeout = 2
    try:
        conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        conn.settimeout(timeout)
        conn.connect((host, port))

        # start and end block - used to define start and End of message in MLLP
        start_block = '\x0b'
        end_block = '\x1c'
        carriage_return = b'\x0d'

        # create message here
        msg = start_block + message + end_block + carriage_return

        print "sending message " + str(msg)
        logging.info("Sending message: "+msg)
        # send message
        conn.send(msg)

        # recieve ack / nack message (buffer size 4096)
        try:
            ack = conn.recv(4096)
            if ack:
                writeAck = "Recieved a reply: "
                writeAck = writeAck + " " + ack
                print writeAck
                logging.info(writeAck)

            else:
                print "No reply  "
                logging.debug("No reply for the message")
        except BaseException as e:

            print "Exception in python script " + e.message
        conn.close()
    # send an exception if connection fails
    except socket.error:
        print "This triggered an exception: Host maybe down, HL7 2.x not discovered on port " + str(port)


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 2.x Message sender')
    parser.add_argument('-ip', '--ip', required=True, metavar='', help='Enter the IP address to scan')
    parser.add_argument('-p', '--port', required=True, metavar='', help='Enter port value')
    parser.add_argument('-m', '--message', metavar='', help='Enter the message')

    args = parser.parse_args()

    logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG, filemode='a+')
    logging.info('__Starting logging for HL7 messenger__')

    if args.ip:

        host = args.ip
        print host

        if float(args.port) == 0 or not args.port:
               print "Enter a port"
        else:
            sendMessage(host,int(args.port),args.message)