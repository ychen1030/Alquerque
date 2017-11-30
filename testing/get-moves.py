
# A Python 3 utility to extract things that look like moves from 
# a transcript of a text session of qirkat, printing them out as a 
# list of move commands suitable for using in a test script (for example).
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

for line in inp:
    re.sub(r'.*:\s*', '', line)
    if match(r'.*(White|Black) moves (.*)\.', line):
        print(group(2))
    elif match(r'.*\s([a-e][1-5](-[a-e][1-5])+)\.?\s*$', line):
        print(group(1))
