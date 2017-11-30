
# A Python 3 utility to prepare a literal testing script from a sequence of
# commands to the qirkat program.  

import re, sys
from subprocess import Popen, DEVNULL, PIPE, STDOUT
from threading import Thread
from queue import Queue, Empty, Full
from os.path import basename

EOF = object()
TIMEOUT = 1.0

def get_output_queue(title, stream):
    queue = Queue(500)
    def reader():
        while True:
            line = stream.readline()
            if line == '':
                queue.put(EOF)
                return
            line = re.sub(r'^.*:\s*', '', line)
            line = re.sub(r'\s+\n', '\n', line)
            line = re.sub('\t', ' ', line)
            line = re.sub('  +', ' ', line)
            if re.match(r'CS61B Qirkat!|\s*$', line):
                continue
            queue.put(line)
    th = Thread(target=reader, name=title, daemon=True)
    th.start()
    return queue, th

command, scriptname = sys.argv[1:3]

prog = Popen(command, universal_newlines=True, stdin=PIPE, stdout=PIPE,
             shell=True, stderr=STDOUT)
script = sys.stdin if scriptname == '-' else open(scriptname)

progout, _ = get_output_queue("Program Output", prog.stdout)

print(command)
for line in script:
    print(line, file=prog.stdin, end="")
    print(line, end="")
    prog.stdin.flush()
    while True:
        try:
            response = progout.get(timeout=TIMEOUT)
            if response is EOF:
                sys.exit(0)
            print("@<{}".format(response.rstrip()))
        except Empty:
            break
