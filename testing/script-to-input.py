
# A Python 3 utility to extract the commands indicated by the first section
# of a test script (input to test-qirkat.py).
import sys, re

def match(patn, text):
    global Match
    Match = re.match(patn, text)
    return Match

def group(k):
    return Match.group(k)

if len(sys.argv) <= 1:
    inp = sys.stdin
else:
    inp = open(sys.argv[1])

while True:
    if not match(r'#|\s*$', inp.readline()):
        break

for line in inp:
    if match(r'-{10}', line):
        break
    if not match(r'#|\s*@', line):
        print(line, end='')
