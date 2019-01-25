import argparse
import dpkt
from scapy.all import *
import pprint

def analyzerFile(pcapFilename):

    print "Attempting to open the file: " + str(pcapFileName)
    print(pcapFileName)

    packets = rdpcap(pcapFileName)
    packetSession = packets.sessions()

    for session in packetSession:
        for packet in packetSession[session]:
            print str(packet)



    '''for k, v in packetSession.iteritems():
        for p in v:
            print p
    '''

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 PCAP Analyzer')
    parser.add_argument('-f','--file', required=True,  help='Enter the filename')

    args = parser.parse_args()
    pcapFileName = args.file
    print ""

    analyzerFile(pcapFileName)

