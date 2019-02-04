import argparse
import networkx as nx
from scapy.all import *
import re


def analyzerFile(pcapFilename):

    G = nx.DiGraph(directed=True)

    packets = rdpcap(pcapFileName)
    networkSession = packets.sessions()

    for session in networkSession:
        for packet in networkSession[session]:
            try:
                if packet[TCP]:
                    if str(packet).startswith("b\""):
                        '''
                        print(packet)
                        print("HL7 Data detected")
                        print("Port: "+str(packet[TCP].sport))
                        print("Source IP address: "+str(packet[IP].src))
                        print("Destination IP address: " + str(packet[IP].dst))
                        '''
                        G.add_edge( (str(packet[IP].dst)+":"+str(packet[IP].dport)),(str(packet[IP].src)+":"+str(packet[TCP].sport)))
            except:
                continue

    nx.draw(G, with_labels=True)
    plt.show()
    plt.savefig("networkGraph.png",format="PNG")

if __name__ == '__main__':
    print("im here")
    print("im here")

    parser = argparse.ArgumentParser(description='An HL7 PCAP Analyzer')
    parser.add_argument('-f','--file', required=True,  help='Enter the filename')

    args = parser.parse_args()
    pcapFileName = args.file
    analyzerFile(pcapFileName)



