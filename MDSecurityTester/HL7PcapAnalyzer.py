import argparse
import pyshark
import networkx as nx
import matplotlib.pyplot as plt



def analyzerFile(pcapFilename):

    cap = pyshark.FileCapture(pcapFileName, only_summaries=True)
    #cap = pyshark.FileCapture(pcapFileName)

    G = nx.Graph()

    #print dir(cap[0])

    for hl7Packet in cap:
        if hl7Packet.protocol =="HL7":
            print(hl7Packet._fields['Info'] +" source "+hl7Packet.source +" destination "+hl7Packet.destination)
            G.add_node(str(hl7Packet.source))

    nx.draw(G, with_labels=True)

    plt.show()

def createHl7Graph():
    G = nx.Graph()

    G.add_node(2)
    G.add_node(5)

    G.add_edge(2,5)
    G.add_edge(4,1)

    print(nx.info(G))

    nx.draw(G,with_labels=True)

    plt.show()

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='An HL7 PCAP Analyzer')
    parser.add_argument('-f','--file', required=True,  help='Enter the filename')

    args = parser.parse_args()
    pcapFileName = args.file
    print("")

    #analyzerFile(pcapFileName)
    createHl7Graph()

