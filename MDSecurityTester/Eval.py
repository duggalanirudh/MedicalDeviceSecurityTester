import argparse
import base64

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Takes input of a python expression, gives out the evaluated output')
    parser.add_argument('-e', '--expression', metavar='', help='input value of the expression')

    args = parser.parse_args()
    evalInput = args.expression

    evalInput = base64.b64decode(evalInput)

    evaloutput = eval(evalInput)

    print evaloutput