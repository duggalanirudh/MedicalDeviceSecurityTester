import argparse
import pyshark
import networkx as nx
import matplotlib.pyplot as plt



def analyzerFile(pcapFilename):

    cap = pyshark.FileCapture(pcapFileName, only_summaries=True)
    #cap = pyshark.FileCapture(pcapFileName)

    print dir(cap[0])

    for hl7Packet in cap:
        if hl7Packet.protocol =="HL7":
            print hl7Packet._fields['Info'] +" source "+hl7Packet.source +" destination "+hl7Packet.destination

def createHl7Graph():
   

    nx.draw(G)
    plt.savefig("simple_path.png")  # save as png
    plt.show()  # display


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 PCAP Analyzer')
    parser.add_argument('-f','--file', required=True,  help='Enter the filename')

    args = parser.parse_args()
    pcapFileName = args.file
    print ""

    #analyzerFile(pcapFileName)
    createHl7Graph()

