import socket
import logging
import datetime
import argparse

LOG_FILENAME = str(datetime.datetime.now()) + "-scan" + ".log"

message = ["msh", "MSH", "MSH|^~\&|ADT1|MCM|LABADT|MCM|198808181126|SECURITY|ADT^A01|MSG00001-|P|2.6"]

def sendMessage(host,port):

    try:

        for currentMessage in message:

            conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            conn.settimeout(timeout)
            conn.connect((host, port))

            # start and end block - used to define start and End of message in MLLP
            start_block = '\x0b'
            end_block = '\x1c'
            carriage_return = b'\x0d'

            # create message here
            msg = start_block + currentMessage + end_block + carriage_return

            print("message sent: " + str(msg))
            # send message
            conn.send(msg)


            # recieve ack / nack message (buffer size 4096)
            try:

                ack = conn.recv(4096)

                if ack:

                    writeAck = "Found HL7 port at IPAddrresss: " + host + " on port: " + str(port)
                    writeAck = writeAck + " message " + currentMessage
                    logging.debug(writeAck)

                    print(writeAck)

                else:
                    print("Not found on port: " + str(port))
            except BaseException as e:
                continue
                print("Exception in python script " + e.message)
            conn.close()
    # send an exception if connection fails
    except socket.error:
        print("This triggered an exception: Host maybe down, HL7 2.x not discovered on port " + str(port))


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 protocol port scanner')
    parser.add_argument('-ip','--ip', help='Enter the IP address to scan')
    parser.add_argument('-p','--port', type=int, help='specify one or more port, enter 0 to scan all ports')
    parser.add_argument('-t','--timeout', help='enter time in seconds')

    args = parser.parse_args()

    timeout = float(args.timeout)

    if args.ip:

        host = args.ip

        print(host)
        print(args.ip)

        logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)
        logging.debug('Starting Scan for IP address: '+str(host))
        print("____Starting Scan____")
        print(("Starting Scan for IP address "+str(host)))

        if float(args.port) == 0 or not args.port:

            for port in range(1024, 1034, 1):
                sendMessage(host,port)
        else:
            sendMessage(host,args.port)